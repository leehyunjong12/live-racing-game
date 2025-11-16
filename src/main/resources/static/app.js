const startButton = document.getElementById('startButton');
const resetButton = document.getElementById('resetButton');
const canvas = document.getElementById('raceCanvas');
const ctx = canvas.getContext('2d');
const JAIL_COORDS = { x: 50, y: 450 };

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
    socket.send(`START:${carNames.join(',')}:${rounds}`);
});

resetButton.addEventListener('click', () => {
    cars = {};
    draw();
    startButton.disabled = false;
    startButton.textContent = "경주 시작!";
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

    } else if (data.type === "WINNER") {
        displayWinner(data.winners);
        startButton.disabled = false;
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
    alert(`🏆 최종 우승자: ${winners.join(', ')} 🏆`);
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
    switch (tileType) {
        case "OBSTACLE":
        case "JAIL":
            return "#FF4136";
        case "MOVE_BACK_NODE":
            return "#FF851B";
        case "MOVE_TO_START":
            return "#0074D9";
        case "MOVE_TO_MIDPOINTS":
            return "#7FDBFF";
        case "SLIDE":
            return "#FFDC00"
        case "NORMAL":
        default:
            return "#FFFFFF"; // 흰색 (일반)
    }
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
    let jailCount = 0; // 무인도에 갇힌 차들의 "줄"

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
