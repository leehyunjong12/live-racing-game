const startButton = document.getElementById('startButton');
const resetButton = document.getElementById('resetButton');
const canvas = document.getElementById('raceCanvas');
const roundCounter = document.getElementById('roundCounter');
const winnerBoard = document.getElementById('winnerBoard');
const winnerList = document.getElementById('winnerList');
const ctx = canvas.getContext('2d');
const JAIL_COORDS = { x: 50, y: 450 };
const NODE_INFO = {
    "NORMAL": {
        color: "#FFFFFF",
        description: "일반"
    },
    "JAIL": {
        color: "#FF4136",
        description: "2턴동안 감옥 (30% 확률)"
    },
    "MOVE_BACK_NODE": {
        color: "#FF851B",
        description: "현재 숫자 -2 위치로 (30% 확률)"
    },
    "MOVE_TO_START": {
        color: "#0074D9",
        description: "처음으로"
    },
    "MOVE_TO_MIDPOINTS": {
        color: "#7FDBFF",
        description: "중간지점중 랜덤 이동"
    },
    "SLIDE": {
        color: "#FFDC00",
        description: "슬라이드"
    }
};
let totalRounds = 0;

let TRACK_MAP = {};
let TRACK_LINES = [];
let cars = {};
const carColors = ["#d9534f", "#5cb85c", "#0275d8", "#f0ad4e", "#5bc0de"];

const socket = new WebSocket("ws://localhost:8080/ws/race");

socket.onopen = function(event) {
    console.log("서버에 연결되었습니다.");

};

socket.onclose = function(event) {
    console.log("서버와 연결이 끊겼습니다.");
    startButton.disabled = true;
    startButton.textContent = "서버 연결 중...";
};

socket.onerror = function(error) {
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
    roundCounter.textContent = "남은 라운드: -";
    totalRounds = 0;
});

socket.onmessage = function(event) {
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
        const { name, position,turnsToSkip } = carState;
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
    if (winners.length === 0) {
        alert("🏆 아무도 결승선에 도착하지 못했습니다! 🏆");
    } else {
        alert(`🏆 최종 우승자: ${winners.join(', ')} 🏆`);
    }
    winnerList.innerHTML = '';

    if (winners.length === 0) {
        const li = document.createElement('li');
        li.textContent = "No Winners";
        li.style.color = "#ccc";
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

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawTrack();
    drawJailNode();
    drawCars();
    requestAnimationFrame(draw);
}

function drawTrack() {
    ctx.strokeStyle = "#888";
    ctx.lineWidth = 3;
    TRACK_LINES.forEach(line => {
        const start = TRACK_MAP[line[0]];
        const end = TRACK_MAP[line[1]];
        if (start && end) {
            ctx.beginPath();
            ctx.moveTo(start.x, start.y);
            ctx.lineTo(end.x, end.y);
            ctx.stroke();
        }
    });

    Object.values(TRACK_MAP).forEach(node => {
        ctx.fillStyle = getNodeColor(node.type);
        ctx.beginPath();
        ctx.arc(node.x, node.y, 10, 0, 2 * Math.PI);
        ctx.fill();

        ctx.fillStyle = "#000";
        ctx.font = "10px Arial";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText(node.id, node.x, node.y);
    });
}
function getNodeColor(tileType) {
    const info = NODE_INFO[tileType];
    if (info) {
        return info.color;
    }
    return NODE_INFO["NORMAL"].color;
}
function drawJailNode() {
    ctx.fillStyle = "#FF0000";
    ctx.beginPath();
    ctx.arc(JAIL_COORDS.x, JAIL_COORDS.y, 10, 0, 2 * Math.PI);
    ctx.fill();

    ctx.fillStyle = "#FFF";
    ctx.font = "10px Arial";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText("JAIL", JAIL_COORDS.x, JAIL_COORDS.y);
}

function drawCars() {
    let jailCount = 0;

    Object.values(cars).forEach(car => {

        if (car.turnsToSkip > 0) {
            const x = JAIL_COORDS.x + 30 + (jailCount * 30);
            const y = JAIL_COORDS.y;

            drawCar(car, x, y);

            ctx.fillStyle = "red";
            ctx.font = "bold 12px Arial";
            ctx.fillText(`SKIP (${car.turnsToSkip})`, x, y + 15);

            jailCount++;

        } else {

            drawCar(car, car.x, car.y);
        }
    });
}


function drawCar(car, x, y) {
    ctx.fillStyle = car.color;
    ctx.beginPath();
    ctx.arc(x, y, 8, 0, 2 * Math.PI);
    ctx.fill();

    ctx.fillStyle = "#FFF";
    ctx.font = "10px Arial";
    ctx.fillText(car.name, x, y - 15);
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
