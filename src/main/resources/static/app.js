/* global Swal */
const canvas = document.getElementById('raceCanvas');
const roundCounter = document.getElementById('roundCounter');
const prizeSection = document.getElementById('prizeSection');
const totalPrizeDisplay = document.getElementById('totalPrizeDisplay');
const winnerSection = document.getElementById('winnerSection');
const boardWinnerList = document.getElementById('boardWinnerList');
const resultModal = document.getElementById('resultModal');
const modalWinnerList = document.getElementById('modalWinnerList');
const closeModalBtn = document.getElementById('closeModalBtn');
const countdownDisplay = document.getElementById('countdownDisplay');
const gameLogContainer = document.getElementById('gameLogContainer');
const gameLogBody = document.getElementById('gameLogBody');
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
let isAutoResetDone = false;
let isRacing = false;
let totalRounds = 0;
const NODE_RADIUS = 15;
let TRACK_MAP = {};
let TRACK_LINES = [];
let cars = {};
let currentUser = null;
const CAR_STYLE = {
    MY_CAR: "#FFD700",   // 내 차: 황금색
    ADMIN: "#000000",    // 운영자: 검정색
    OTHERS: "#BDC3C7"    // 다른 차: 회색
};

const socket = new WebSocket("ws://localhost:8080/ws/race");

socket.onopen = function (event) {
    console.log("서버에 연결되었습니다.");

};

socket.onclose = function (event) {
    console.log("서버와 연결이 끊겼습니다.");
};

socket.onerror = function (error) {
    console.error("WebSocket 오류 발생:", error);
};

socket.onmessage = function (event) {
    const data = JSON.parse(event.data);

    if (data.type === "MAP_LAYOUT") {
        console.log("맵 레이아웃 수신 완료.");
        TRACK_MAP = data.nodes;
        TRACK_LINES = data.lines;

        draw();
        isRacing = false;

    } else if (data.type === "RACING") {

        updateCarPositions(data.cars);
        isRacing = true;
        isAutoResetDone = true;
        totalRounds = data.totalRounds;
        const remainingRounds = totalRounds - data.round;
        roundCounter.textContent = `남은 라운드: ${remainingRounds}`;
        roundCounter.style.color = "#27ae60";
        gameLogBody.innerHTML = '';
        if (data.events && data.events.length > 0) {
            addLogMessages(data.events);
        }
        if (gameLogBody.children.length > 0) {
            gameLogContainer.style.display = 'block';
        } else {
            gameLogContainer.style.display = 'none';
        }

    } else if (data.type === "WINNER") {
        isRacing = false;
        totalRounds = 0;
        displayWinner(data.winners);
        checkLoginStatus();
    }
};
function addLogMessages(messages) {
    if (!currentUser) return;

    messages.forEach(msg => {
        const [type, carName] = msg.split(':');
        if (!carName.includes(currentUser)) return;

        const div = document.createElement('div');
        div.className = 'log-entry';
        const circle = document.createElement('span');
        circle.className = 'log-circle';

        const nodeInfo = NODE_INFO[type] || NODE_INFO["NORMAL"];
        circle.style.backgroundColor = nodeInfo.color;

        const text = document.createElement('span');
        text.className = 'log-text';
        text.textContent = carName;

        div.appendChild(circle);
        div.appendChild(text);

        gameLogBody.appendChild(div);
    });

    if (gameLogBody.lastChild) {
        gameLogBody.lastChild.scrollIntoView({ behavior: "smooth" });
    }
}
function updateCarPositions(carStates) {

    carStates.forEach((carState, index) => {
        const {name, position, turnsToSkip} = carState;
        const targetCoords = TRACK_MAP[position];
        if (!targetCoords) return;
        let assignedColor;
        if (name === "Admin_Bot") {
            assignedColor = CAR_STYLE.ADMIN;
        } else if (currentUser && name.startsWith(currentUser + "_")) {
            assignedColor = CAR_STYLE.MY_CAR;
        } else {
            assignedColor = CAR_STYLE.OTHERS;
        }
        if (!cars[name]) {
            cars[name] = {
                name: name,
                x: targetCoords.x,
                y: targetCoords.y,
                color: assignedColor,
                turnsToSkip: turnsToSkip
            };
        } else {
            cars[name].x = targetCoords.x;
            cars[name].y = targetCoords.y;
            cars[name].turnsToSkip = turnsToSkip;
            cars[name].color = assignedColor;
        }
    });
}


function displayWinner(winners) {
    const adminWins = winners.includes("Admin_Bot");

    if (adminWins && winners.length === 1) {
        Swal.fire({
            icon: 'info',
            title: '🤖 Admin_Bot 승리!',
            html: `
                <b style="color: red;">플레이어 전원 패배!</b><br>
                <span style="font-size: 0.9em;">판돈은 다음 경기로 <b>이월(Carry Over)</b>됩니다.</span>
            `,
            confirmButtonText: '다음 기회에...',
            background: '#fff',
            backdrop: `rgba(0,0,0,0.8)`
        });

    } else if (adminWins && winners.length > 1) {
        shootConfetti();
        Swal.fire({
            icon: 'warning',
            title: '🤝 공동 우승!',
            html: `
                Admin과 비겼습니다!<br>
                상금은 <b style="color: blue;">1/${winners.length}</b>로 나뉩니다.<br>
                (Admin 몫은 회수됩니다)
            `,
            confirmButtonText: '확인'
        });

    } else {
        if (winners.length > 0) {
            shootConfetti();
            showResultModal(winners);
        } else {
            Swal.fire({
                icon: 'error',
                title: 'TIME OVER',
                text: '아무도 결승선에 도착하지 못했습니다.',
                confirmButtonText: '아쉽네요'
            });
        }
    }
    showWinnerMode(winners);
    checkLoginStatus();
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

function showPrizeMode() {
    winnerSection.style.display = 'none';
    prizeSection.style.display = 'block';
    fetchPrize();
}

function showWinnerMode(winners) {
    boardWinnerList.innerHTML = '';
    if (winners.length === 0) {
        boardWinnerList.innerHTML = '<li>No Winners</li>';
    } else {
        winners.forEach(name => {
            const li = document.createElement('li');
            if (name === "Admin_Bot") {
                li.textContent = `🤖 ${name}`;
                li.style.borderLeftColor = "#333";
            } else {
                li.textContent = `🥇 ${name}`;
            }
            boardWinnerList.appendChild(li);
        });
    }

    prizeSection.style.display = 'none';
    winnerSection.style.display = 'block';

    setTimeout(() => {
        showPrizeMode();
    }, 30000);
}

function fetchPrize() {
    fetch('/api/prize/pot')
        .then(res => res.json())
        .then(data => {
            totalPrizeDisplay.textContent = `${data.amount.toLocaleString()} ₩`;
        })
        .catch(() => {
            totalPrizeDisplay.textContent = "0 ₩";
        });
}
function startNextRaceTimer() {
    setInterval(() => {
        if (isRacing) return;
        const now = new Date();
        const minutes = now.getMinutes();
        const seconds = now.getSeconds();

        const nextFiveMin = (Math.floor(minutes / 5) + 1) * 5;
        const diffMinutes = nextFiveMin - minutes - 1;
        const diffSeconds = 60 - seconds;

        let displayMin = diffMinutes;
        let displaySec = diffSeconds;
        if (displaySec === 60) {
            displaySec = 0;
            displayMin += 1;
        }
        const totalSecondsLeft = (displayMin * 60) + displaySec;

        if (totalSecondsLeft <= 10 && totalSecondsLeft > 0) {
            setInputsDisabled(true);
            if (totalSecondsLeft <= 3) {
                if (totalSecondsLeft === 3) showCountdown("3", "#e74c3c");
                if (totalSecondsLeft === 2) showCountdown("2", "#e67e22");
                if (totalSecondsLeft === 1) showCountdown("1", "#f1c40f");
            }

            roundCounter.style.color = "#e74c3c";
            roundCounter.textContent = `다음 경주까지: ${displaySec}초`;
            if (!isAutoResetDone) {
                performTrackReset();
                isAutoResetDone = true;
                }
        } else {
            if (!isRacing) {
                setInputsDisabled(false);
                roundCounter.style.color = "#2c3e50";
                const fmtMin = displayMin.toString().padStart(2, '0');
                const fmtSec = displaySec.toString().padStart(2, '0');
                roundCounter.textContent = `다음 경주까지: ${fmtMin}:${fmtSec}`;
                isAutoResetDone = false;
            }
        }
    }, 1000);
}
function performTrackReset() {
    console.log("♻️ 경기 화면 자동 초기화");
    cars = {};
    draw();
    resultModal.style.display = 'none';

    showPrizeMode();
    gameLogBody.innerHTML = '';
    gameLogContainer.style.display = 'none';
}

function setInputsDisabled(isDisabled) {
    if (authBar.btnCharge) authBar.btnCharge.disabled = isDisabled;
    if (authBar.btnRegisterCar) authBar.btnRegisterCar.disabled = isDisabled;
    if (authBar.btnLogout) authBar.btnLogout.disabled = isDisabled;

    const opacity = isDisabled ? "0.5" : "1";
    if (authBar.btnCharge) authBar.btnCharge.style.opacity = opacity;
    if (authBar.btnRegisterCar) authBar.btnRegisterCar.style.opacity = opacity;
    if (authBar.btnLogout) authBar.btnLogout.style.opacity = opacity;


    if (isDisabled && (Swal.isVisible() || resultModal.style.display === 'flex')) {
        Swal.close();
        closeAllModals();
    }
}
function showCountdown(text, color = "#FFD700") {
    countdownDisplay.textContent = text;
    countdownDisplay.style.color = color;
    countdownDisplay.style.display = "block";

    countdownDisplay.classList.remove("animate-count");
    void countdownDisplay.offsetWidth;
    countdownDisplay.classList.add("animate-count");

    setTimeout(() => {
        countdownDisplay.style.display = "none";
    }, 900);
}
document.addEventListener('DOMContentLoaded', (event) => {
    populateLegend();
    checkLoginStatus();
    showPrizeMode();
    startNextRaceTimer();
});

const authBar = {
    loggedOut: document.getElementById('loggedOutView'),
    loggedIn: document.getElementById('loggedInView'),
    btnCharge: document.getElementById('btnCharge'),
    btnRegisterCar: document.getElementById('btnRegisterCar'),
    carCountDisplay: document.getElementById('carCountDisplay'),
    userDisplay: document.getElementById('userDisplay'),
    btnLogout: document.getElementById('btnLogout')
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
        Swal.fire({
            icon: 'warning',
            title: '잠깐!',
            text: '아이디와 비밀번호를 모두 입력해주세요.',
            confirmButtonText: '알겠어요'
        });
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
                Swal.fire({
                    icon: 'success',
                    title: '가입 성공!',
                    text: '이제 로그인해주세요.',
                    confirmButtonText: '확인'
                }).then(() => {
                    openModal('login');
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: '가입 실패',
                    text: msg,
                    confirmButtonText: '다시 시도'
                });
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
                Swal.fire({
                    icon: 'success',
                    title: '환영합니다!',
                    text: username + '님, 환영합니다.',
                    timer: 1500,
                    showConfirmButton: false
                });
                closeAllModals();
                checkLoginStatus();
            } else {
                Swal.fire({
                    icon: 'error',
                    title: '로그인 실패',
                    text: msg,
                    confirmButtonText: '확인'
                });
            }
        })
        .catch(err => console.error(err));
});

document.getElementById('btnLogout').addEventListener('click', () => {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => {
            currentUser = null
            Swal.fire({
                icon: 'info',
                title: '로그아웃',
                text: '안녕히 가세요!',
                timer: 1000,
                showConfirmButton: false
            });
            updateAuthUI(false);
        });
});
authBar.btnCharge.addEventListener('click', () => {
    fetch('/api/payment/balance')
        .then(res => res.json())
        .then(async data => {
            const currentBalance = data.balance;

            const { value: amount } = await Swal.fire({
                title: '내 지갑',
                html: `
                    <div style="font-size: 1.2em; margin-bottom: 10px;">
                        현재 잔액: <b style="color: #27ae60;">${currentBalance.toLocaleString()}원</b>
                    </div>
                    <div style="font-size: 0.9em; color: #666;">얼마를 충전할까요?</div>
                `,
                input: 'number',
                inputValue: 50000,
                inputAttributes: {
                    min: '50000',
                    step: '50000',
                    placeholder: '금액 입력 (예: 50000)'
                },
                showCancelButton: true,
                confirmButtonText: '충전하기',
                cancelButtonText: '닫기',
                inputValidator: (value) => {
                    if (!value || value <= 0) {
                        return '올바른 금액을 입력해주세요!';
                    }
                }
            });
            if (amount) {
                chargeBalance(amount);
            }
        })
        .catch(() => {
            Swal.fire('오류', '잔액 정보를 불러오지 못했습니다.', 'error');
        });
});

function chargeBalance(amount) {
    fetch('/api/payment/charge', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount: parseInt(amount) })
    })
        .then(async response => {
            if (response.ok) {
                const data = await response.json();
                Swal.fire({
                    icon: 'success',
                    title: '충전 완료!',
                    html: `충전 후 잔액: <b>${data.balance.toLocaleString()}원</b>`,
                    timer: 2000,
                    showConfirmButton: false
                });
            } else {
                Swal.fire('충전 실패', '오류가 발생했습니다.', 'error');
            }
        });
}
authBar.btnRegisterCar.addEventListener('click', async () => {

    const { value: quantity } = await Swal.fire({
        title: '레이싱 참가 신청',
        html: `
            <p>참가비: <b>1대당 50,000원</b></p>
            <p style="font-size:0.9em; color:#666;">자동으로 닉네임_번호로 등록됩니다.</p>
        `,
        input: 'number',
        inputValue: 1,
        inputAttributes: {
            min: '1',
            max: '10',
            step: '1'
        },
        showCancelButton: true,
        confirmButtonText: '등록 및 결제',
        cancelButtonText: '취소',
        inputValidator: (value) => {
            if (!value || value <= 0) return '최소 1대 이상 입력해야 합니다!';
        }
    });

    if (quantity) {
        const totalCost = quantity * 50000;

        const confirm = await Swal.fire({
            title: '결제 확인',
            html: `
                총 <b>${quantity}대</b>를 등록하시겠습니까?<br>
                참가비 <b>${totalCost.toLocaleString()}원</b>이 차감됩니다.
            `,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '네, 결제합니다!'
        });

        if (confirm.isConfirmed) {
            registerCarsAPI(parseInt(quantity));
        }
    }
});

function registerCarsAPI(quantity) {
    fetch('/api/cars/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ quantity: quantity })
    })
        .then(async response => {
            const data = await response.json();
            if (response.ok) {
                Swal.fire('등록 성공!', `${quantity}대가 출전 명단에 올랐습니다.`, 'success');
                fetchPrize();
                checkLoginStatus();
            } else {
                Swal.fire('등록 실패', data.error || '오류가 발생했습니다.', 'error');
            }
        })
        .catch(() => Swal.fire('오류', '서버와 통신할 수 없습니다.', 'error'));
}

function updateAuthUI(isLoggedIn, username = '',carCount = 0) {
    if (isLoggedIn) {
        authBar.loggedOut.style.display = 'none';
        authBar.loggedIn.style.display = 'flex';
        authBar.userDisplay.textContent = `👤 ${username}`;
        authBar.carCountDisplay.textContent = `🚘 ${carCount}대`;
    } else {
        authBar.loggedOut.style.display = 'flex';
        authBar.loggedIn.style.display = 'none';
    }
}
function checkLoginStatus() {
    fetch('/api/auth/me', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error("로그인 안 됨");
        })
        .then(data => {
            const username = data.username;
            currentUser = username;
            const carCount = data.carCount || 0;
            updateAuthUI(true, username,carCount);
        })
        .catch(() => {
            updateAuthUI(false);
        });
}