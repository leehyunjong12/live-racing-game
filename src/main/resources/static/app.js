const startButton = document.getElementById('startButton');
const resetButton = document.getElementById('resetButton');
const canvas = document.getElementById('raceCanvas');
const roundCounter = document.getElementById('roundCounter');
const winnerBoard = document.getElementById('winnerBoard');
const winnerList = document.getElementById('winnerList');
const resultModal = document.getElementById('resultModal');
const modalWinnerList = document.getElementById('modalWinnerList');
const closeModalBtn = document.getElementById('closeModalBtn');
const ctx = canvas.getContext('2d');
const JAIL_COORDS = {x: 50, y: 450};
const NODE_INFO = {
    "NORMAL": {color: "#ecf0f1", border: "#bdc3c7", description: "일반"},
    "JAIL": {color: "#e74c3c", border: "#c0392b", description: "2턴 감옥 (30% 확률)"},
    "MOVE_BACK_NODE": {color: "#e67e22", border: "#d35400", description: "현재 노드 -2 위치로 (30% 확률)"},
    "MOVE_TO_START": {color: "#3498db", border: "#2980b9", description: "출발점 복귀"},
    "MOVE_TO_MIDPOINTS": {color: "#9b59b6", border: "#8e44ad", description: "중간 지점 무작위 이동"},
    "SLIDE": {color: "#f1c40f", border: "#f39c12", description: "슬라이드"}
};
const INFINITE_LOOP_PATH = [7, 4, 10, 7];
const LOOP_SEGMENTS = [
    [7, 4],
    [4, 10],
    [10, 7]
];
let dashOffset = 0;
let totalRounds = 0;
const NODE_RADIUS = 15;
let TRACK_MAP = {};
let TRACK_LINES = [];
let cars = {};
const carColors = ["#d9534f", "#5cb85c", "#0275d8", "#f0ad4e", "#5bc0de"];

const socket = new WebSocket("ws://localhost:8080/ws/race");

socket.onopen = function (event) {
    console.log("서버에 연결되었습니다.");

};

socket.onclose = function (event) {
    console.log("서버와 연결이 끊겼습니다.");
    startButton.disabled = true;
    startButton.textContent = "서버 연결 중...";
};

socket.onerror = function (error) {
    console.error("WebSocket 오류 발생:", error);
};
startButton.addEventListener('click', () => {
    startButton.disabled = true;
    cars = {};

    const carNames = ["Pobi", "Crong", "Honux", "JK", "Luffy"];
    const rounds = 50;

    totalRounds = rounds;
    roundCounter.textContent = `남은 라운드: ${totalRounds}`;
    socket.send(`START:${carNames.join(',')}:${rounds}`);
});

resetButton.addEventListener('click', () => {
    cars = {};
    draw();
    startButton.disabled = false;
    startButton.textContent = "경주 시작!";
    winnerBoard.style.display = 'none';
    resultModal.style.display = 'none';
    roundCounter.textContent = "남은 라운드: -";
    totalRounds = 0;
});

socket.onmessage = function (event) {
    const data = JSON.parse(event.data);

    if (data.type === "MAP_LAYOUT") {
        console.log("맵 레이아웃 수신 완료.");
        TRACK_MAP = data.nodes;
        TRACK_LINES = data.lines;

        draw();
        startButton.disabled = false;
        startButton.textContent = "경주 시작!";

    } else if (data.type === "RACING") {
        updateCarPositions(data.cars);
        const remainingRounds = totalRounds - data.round;
        roundCounter.textContent = `남은 라운드: ${remainingRounds}`;

    } else if (data.type === "WINNER") {
        displayWinner(data.winners);
        startButton.disabled = false;
        roundCounter.textContent = "남은 라운드: 0";
    }
};

function updateCarPositions(carStates) {

    carStates.forEach((carState, index) => {
        const {name, position, turnsToSkip} = carState;
        const targetCoords = TRACK_MAP[position];
        if (!targetCoords) return;

        if (!cars[name]) {
            cars[name] = {
                name: name,
                x: targetCoords.x,
                y: targetCoords.y,
                color: carColors[index % carColors.length],
                turnsToSkip: turnsToSkip
            };
        } else {
            cars[name].x = targetCoords.x;
            cars[name].y = targetCoords.y;
            cars[name].turnsToSkip = turnsToSkip;
        }
    });
}

function displayWinner(winners) {
    showResultModal(winners);

    if (winners.length > 0) {
        shootConfetti();
    }
    updateSideBoard(winners);
}

function showResultModal(winners) {
    modalWinnerList.innerHTML = '';

    if (winners.length === 0) {
        modalWinnerList.innerHTML = '<div class="modal-winner-name" style="color: gray;">No Winners...</div>';
    } else {
        winners.forEach(name => {
            const div = document.createElement('div');
            div.className = 'modal-winner-name';
            div.textContent = `🥇 ${name}`;
            modalWinnerList.appendChild(div);
        });
    }
    resultModal.style.display = 'flex';
}

function updateSideBoard(winners) {
    winnerList.innerHTML = '';
    if (winners.length === 0) {
        const li = document.createElement('li');
        li.textContent = "No Winners";
        winnerList.appendChild(li);
    } else {
        winners.forEach(name => {
            const li = document.createElement('li');
            li.textContent = `🥇 ${name}`;
            winnerList.appendChild(li);
        });
    }
    winnerBoard.style.display = 'block';
}

closeModalBtn.addEventListener('click', () => {
    resultModal.style.display = 'none';
});

function shootConfetti() {
    const duration = 3000;
    const end = Date.now() + duration;

    (function frame() {
        confetti({
            particleCount: 5,
            angle: 60,
            spread: 55,
            origin: {x: 0}
        });
        confetti({
            particleCount: 5,
            angle: 120,
            spread: 55,
            origin: {x: 1}
        });

        if (Date.now() < end) {
            requestAnimationFrame(frame);
        }
    })();
}

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawTrack();
    drawLoopWarning();
    drawJailNode();
    drawCars();
    requestAnimationFrame(draw);
}

function drawTrack() {
    ctx.strokeStyle = "#555";
    ctx.lineWidth = 3;
    ctx.lineCap = "round";
    ctx.setLineDash([]);

    TRACK_LINES.forEach(line => {
        const isLoopSegment = LOOP_SEGMENTS.some(s =>
            (s[0] === line[0] && s[1] === line[1]) || (s[0] === line[1] && s[1] === line[0])
        );
        if (isLoopSegment) {
            return;
        }
        const start = TRACK_MAP[line[0]];
        const end = TRACK_MAP[line[1]];
        if (start && end) {
            const endpoints = calculateLineEndpoints(start, end, NODE_RADIUS);

            ctx.beginPath();
            ctx.moveTo(endpoints.x1, endpoints.y1);
            ctx.lineTo(endpoints.x2, endpoints.y2);
            ctx.stroke();
        }
    });
    ctx.setLineDash([]);
    Object.values(TRACK_MAP).forEach(node => {
        const info = getNodeInfo(node.type);

        ctx.fillStyle = info.color;
        ctx.beginPath();
        ctx.arc(node.x, node.y, NODE_RADIUS, 0, 2 * Math.PI);
        ctx.fill();

        ctx.strokeStyle = info.border;
        ctx.lineWidth = 2;
        ctx.stroke();
        ctx.fillStyle = "#333";
        ctx.font = "bold 12px 'Patrick Hand'";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText(node.id, node.x, node.y);
    });
}

function getNodeInfo(tileType) {
    return NODE_INFO[tileType] || NODE_INFO["NORMAL"];
}

function drawJailNode() {
    const info = NODE_INFO["JAIL"];
    ctx.fillStyle = info.color;
    ctx.beginPath();
    ctx.arc(JAIL_COORDS.x, JAIL_COORDS.y, 15, 0, 2 * Math.PI);
    ctx.fill();

    ctx.strokeStyle = info.border;
    ctx.lineWidth = 3;
    ctx.stroke();
    ctx.fillStyle = "#FFF";
    ctx.font = "bold 12px 'Patrick Hand'";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText("JAIL", JAIL_COORDS.x, JAIL_COORDS.y);
}

function drawLoopWarning() {
    dashOffset += 1.2;

    ctx.strokeStyle = "rgba(85, 85, 85, 0.8)";
    ctx.lineWidth = 3;
    ctx.setLineDash([20, 10]);
    ctx.lineDashOffset = -dashOffset;

    for (let i = 0; i < INFINITE_LOOP_PATH.length - 1; i++) {
        const start = TRACK_MAP[INFINITE_LOOP_PATH[i]];
        const end = TRACK_MAP[INFINITE_LOOP_PATH[i + 1]];

        if (start && end) {
            const endpoints = calculateLineEndpoints(start, end, NODE_RADIUS);

            ctx.beginPath();
            ctx.moveTo(endpoints.x1, endpoints.y1);
            ctx.lineTo(endpoints.x2, endpoints.y2);
            ctx.stroke();
        }
    }

    ctx.setLineDash([]);
    ctx.lineWidth = 3;
}

function calculateLineEndpoints(startCoords, endCoords, radius) {
    const dx = endCoords.x - startCoords.x;
    const dy = endCoords.y - startCoords.y;
    const dist = Math.sqrt(dx * dx + dy * dy);

    const unitX = dx / dist;
    const unitY = dy / dist;

    const x1 = startCoords.x + unitX * radius;
    const y1 = startCoords.y + unitY * radius;

    const x2 = endCoords.x - unitX * radius;
    const y2 = endCoords.y - unitY * radius;

    return {x1, y1, x2, y2};
}

function drawCars() {
    let jailCount = 0;

    Object.values(cars).forEach(car => {
        if (car.turnsToSkip > 0) {
            const x = JAIL_COORDS.x + 40 + (jailCount * 40);
            const y = JAIL_COORDS.y;

            drawCar(car, x, y);

            ctx.fillStyle = "#e74c3c";
            ctx.font = "bold 14px 'Patrick Hand'";
            ctx.fillText(`SKIP (${car.turnsToSkip})`, x, y + 25);
            jailCount++;
        } else {
            drawCar(car, car.x, car.y);
        }
    });
}

function drawCar(car, x, y) {
    ctx.fillStyle = car.color;
    ctx.beginPath();
    ctx.arc(x, y, 10, 0, 2 * Math.PI);
    ctx.fill();

    ctx.strokeStyle = "#333";
    ctx.lineWidth = 2;
    ctx.stroke();

    ctx.fillStyle = "#333";
    ctx.font = "bold 16px 'Patrick Hand'";
    ctx.textAlign = "center";
    ctx.fillText(car.name, x, y - 18);
}

function populateLegend() {
    const legendList = document.getElementById('legend-list');
    legendList.innerHTML = '';

    for (const typeName in NODE_INFO) {
        const info = NODE_INFO[typeName];

        const li = document.createElement('li');

        const colorSpan = document.createElement('span');
        colorSpan.className = 'legend-color';
        colorSpan.style.backgroundColor = info.color;

        const textNode = document.createTextNode(` ${info.description}`);

        li.appendChild(colorSpan);
        li.appendChild(textNode);
        legendList.appendChild(li);
    }
}

document.addEventListener('DOMContentLoaded', (event) => {
    populateLegend();
});

const authBar = {
    loggedOut: document.getElementById('loggedOutView'),
    loggedIn: document.getElementById('loggedInView'),
};

const modals = {
    login: document.getElementById('loginModal'),
    register: document.getElementById('registerModal')
};

document.getElementById('btnShowLogin').addEventListener('click', () => openModal('login'));
document.getElementById('btnShowRegister').addEventListener('click', () => openModal('register'));
document.querySelectorAll('.btn-close-modal').forEach(btn => {
    btn.addEventListener('click', closeAllModals);
});

document.getElementById('linkToRegister').addEventListener('click', () => openModal('register'));
document.getElementById('linkToLogin').addEventListener('click', () => openModal('login'));

function openModal(type) {
    closeAllModals();
    modals[type].style.display = 'flex';
}

function closeAllModals() {
    modals.login.style.display = 'none';
    modals.register.style.display = 'none';
    document.querySelectorAll('.sketch-input').forEach(input => input.value = '');
}

document.getElementById('btnRegisterAction').addEventListener('click', () => {
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;

    if (!username || !password) {
        alert("아이디와 비밀번호를 입력해주세요.");
        return;
    }

    fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
        .then(async response => {
            const msg = await response.text();
            if (response.ok) {
                alert("가입 성공! 로그인해주세요.");
                openModal('login');
            } else {
                alert("가입 실패: " + msg);
            }
        })
        .catch(err => console.error(err));
});

document.getElementById('btnLoginAction').addEventListener('click', () => {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
        .then(async response => {
            const msg = await response.text();
            if (response.ok) {
                alert("환영합니다! " + username + "님");
                closeAllModals();
                updateAuthUI(true);
            } else {
                alert("로그인 실패: " + msg);
            }
        })
        .catch(err => console.error(err));
});

document.getElementById('btnLogout').addEventListener('click', () => {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            alert("로그아웃 되었습니다.");
            updateAuthUI(false);
        });
});

function updateAuthUI(isLoggedIn) {
    if (isLoggedIn) {
        authBar.loggedOut.style.display = 'none';
        authBar.loggedIn.style.display = 'block';

    } else {
        authBar.loggedOut.style.display = 'block';
        authBar.loggedIn.style.display = 'none';
    }
}