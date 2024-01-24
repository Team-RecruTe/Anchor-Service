// 예약시간이 활동 가능한지 업데이트 (예시: 선택한 날짜와 요일, 활동 가능한 시간, 신청된 시간대)
let selectedDate;
const dayOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY',
  'FRIDAY', 'SATURDAY'];
let activeTimes; /* 주어진 activeTime 데이터 */
let unavailableTimes; /* 주어진 unavailableTimes 데이터 */

document.addEventListener('DOMContentLoaded', () => {
  let currentUri = window.location.pathname;
  let requestUri = currentUri + '/reservation-time';
  axios.get(requestUri)
  .then(res => {
    let data = res.data;
    unavailableTimes = data.unavailableTimes;
    activeTimes = data.activeTimes;
    console.log(unavailableTimes);
    console.log(activeTimes);
  });

  document.querySelector('.tui-datepicker').style.border = 'none';
  document.querySelector('.datepicker-container').style.marginRight = '500px';
  document.querySelector('.tui-calendar-container').style.width = '40rem';
  document.querySelector('.tui-calendar').style.width = '40rem';
});

// modal 생성 및 초기화
let modalElement = document.getElementById('myModal');
document.querySelector('.modal-button').addEventListener('click', () => {
  let modal = new bootstrap.Modal(modalElement);
  datepicker.setDate(new Date());
  modal.show();
});

modalElement.addEventListener('hidden.bs.modal', () => {
  buttonContainer.innerHTML = '';
});

// Datepicker 객체 생성
let datepicker = new tui.DatePicker('#datepicker-wrapper', {
  language: 'ko',
  date: new Date(),
  input: {
    element: '#datepicker-input',
    format: 'yyyy-MM-dd'
  },
  showAlways: true
});

const buttonContainer = document.querySelector('.time-button-group');
let inputTime;
datepicker.on('change', () => {
  buttonContainer.innerHTML = '';
  selectedDate = datepicker.getDate();

  let currentDate = new Date();

  let newCurrentDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate());
  let newSelectedDate = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate());

  if (newCurrentDate <= newSelectedDate) {
    initializeReservationButtons(selectedDate, currentDate);
    // 당일이라면, 현재시간 이전의 신청가능시간은 삭제해야함.
    updateReservationButtons(activeTimes, unavailableTimes, selectedDate);
    clickEventCreator();
  }
});

function clickEventCreator() {
  let inputs = document.querySelectorAll('.time-button-group input');
  inputs.forEach((input) => {
    input.addEventListener('click', () => {
      inputTime = input.value;
    });
  });
}

// 예약시간 버튼을 생성하고 초기화하는 함수
function initializeReservationButtons(selectedDate, currentDate) {

  // active time 에 등록된 시간대만 버튼 생성
  // 선택한 날짜의 요일
  // 선택한 요일의 활동시간 리스트 조회
  let selectedDayOfWeek = dayOfWeek[selectedDate.getDay()];
  let activeTimeInfos = activeTimes[selectedDayOfWeek];
  let newCurrentDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate());
  let newSelectedDate = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate());
  let currentCombinedTime = currentDate.getHours() * 60 + currentDate.getMinutes();
  activeTimeInfos.forEach(activeTimeInfo => {
    let activeStatus = activeTimeInfo.active_status;
    let openTime = activeTimeInfo.open_time;
    let closeTime = activeTimeInfo.close_time;
    let div = document.createElement('div');
    div.classList.add('input-group', 'radio-group');
    if (activeStatus === 'OPEN') {
      if (newCurrentDate.getTime() === newSelectedDate.getTime()) {
        openTime = convertMinutesToTimeString(
            Math.max(convertTimeStringToMinutes(openTime), Math.floor((currentCombinedTime + 30) / 30) * 30));
      }
      for (let time = openTime; time < closeTime; time = increment30Minutes(time)) {
        appendInput(time, div);
        appendLabel(time, div);
      }
      buttonContainer.appendChild(div);
    }
  });

  function convertTimeStringToMinutes(time) {
    let [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  }

  function convertMinutesToTimeString(minutes) {
    let hours = Math.floor(minutes / 60);
    let minutesPart = minutes % 60;
    let formattedHours = String(hours).padStart(2, '0');
    let formattedMinutes = String(minutesPart).padStart(2, '0');
    return `${formattedHours}:${formattedMinutes}`;
  }

  function appendInput(time, div) {
    let input = document.createElement('input');
    input.classList.add('btn-check');
    input.type = 'radio';
    input.name = 'time';
    input.value = time;
    input.id = time;
    div.appendChild(input);
  }

  function appendLabel(time, div) {
    let label = document.createElement('label');
    label.classList.add('radio', 'btn', 'btn-outline-success');
    label.htmlFor = time;
    label.textContent = time;
    div.appendChild(label);
  }
}

let durationTime = document.getElementById('duration-time').value;

// 요일을 확인하고 활동 가능한 시간과 unavailableTimes에 따라 버튼 상태를 조정하는 함수
function updateReservationButtons(activeTimes, unavailableTimes, selectedDate) {
  const buttons = document.querySelectorAll('.radio-group input');
  const labels = document.querySelectorAll('.radio-group label');

  let parseSelectedDate = parseDate(selectedDate);
  let selectedDayOfWeek = dayOfWeek[selectedDate.getDay()];

  let targetUnavailableTime = unavailableTimes[parseSelectedDate];
  if (targetUnavailableTime !== undefined) {
    targetUnavailableTime.forEach((unavailableTime) => {
      let from = unavailableTime.from;
      let to = unavailableTime.to;
      for (let count = 1; count < calculateMinutes(durationTime); count++) {
        from = decrement30Minutes(from);
      }
      for (let time = from; time < to; time = increment30Minutes(time)) {
        removeButton(time);
        removeLabel(time);
      }
    });

    activeTimes[selectedDayOfWeek].forEach(activeTimeInfo => {
      let time = activeTimeInfo.close_time;
      for (let count = calculateMinutes(durationTime); count > 1; count--) {
        removeButton(time);
        removeLabel(time);
        time = decrement30Minutes(time);
      }
    });
  }

  function removeButton(time) {
    buttons.forEach(button => {
      if (button.id === time) {
        button.remove();
      }
    });
  }

  function removeLabel(time) {
    labels.forEach(label => {
      if (label.htmlFor === time) {
        label.remove();
      }
    });
  }
}

// 결제페이지로 넘어가면서 결제진행할 시간 잠금처리하는 함수
document.getElementById('submitButton').addEventListener('click', () => {
  applicationTimeLock();
  document.getElementById('form').submit();
});

function applicationTimeLock() {
  let currentUri = window.location.pathname;
  let requestUri = currentUri + '/lock';
  let inputDate = document.getElementById('datepicker-input').value;
  let inputDurationTime = document.getElementById('duration-time').value;
  axios.post(requestUri, {
    date: inputDate,
    time: inputTime,
    durationTime: inputDurationTime
  }).catch(error => alert(error));
}

//================================Date Function================================//
function parseDate(date) {
  let year = date.getFullYear();
  let month = String(date.getMonth() + 1).padStart(2, '0');
  let day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function calculateMinutes(durationTime) {
  const matches = durationTime.match(/(\d+)h(\d+)m/);
  if (!matches) {
    return;
  }
  const hours = parseInt(matches[1], 10);
  const minutes = parseInt(matches[2], 10);

  const totalMinutes = hours * 60 + minutes;
  return Math.ceil(totalMinutes / 30);
}

function increment30Minutes(timeString) {
  const [hours, minutes] = timeString.split(':').map(Number);
  const newMinutes = (minutes + 30) % 60;
  const newHours = hours + Math.floor((minutes + 30) / 60);
  const formattedHours = String(newHours).padStart(2, '0');
  const formattedMinutes = String(newMinutes).padStart(2, '0');
  return `${formattedHours}:${formattedMinutes}`;
}

function decrement30Minutes(timeString) {
  const [hours, minutes] = timeString.split(':').map(Number);
  const totalMinutes = hours * 60 + minutes;
  const newTotalMinutes = totalMinutes - 30;
  // 음수가 되지않게 처리
  const adjustedMinutes = Math.max(newTotalMinutes, 0);
  const newHours = Math.floor(adjustedMinutes / 60);
  const newMinutes = adjustedMinutes % 60;
  const formattedHours = String(newHours).padStart(2, '0');
  const formattedMinutes = String(newMinutes).padStart(2, '0');
  return `${formattedHours}:${formattedMinutes}`;
}