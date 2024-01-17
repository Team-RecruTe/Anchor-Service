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
document.querySelector('.modal-button').addEventListener('click', (e) => {
  e.preventDefault();

  let modal = new bootstrap.Modal(modalElement);
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
  showToday: false,
  showAlways: true
});

const buttonContainer = document.querySelector('.time-button-group');
let inputTime;
datepicker.on('change', () => {
  buttonContainer.innerHTML = '';

  selectedDate = datepicker.getDate();

  const selectedDay = dayOfWeek[selectedDate.getDay()];
  const parseSelectedDate = parseDate(selectedDate);
  initializeReservationButtons(selectedDate);
  updateReservationButtons(selectedDay, activeTimes,
      unavailableTimes, parseSelectedDate);
  clickEventHandler();
});

function clickEventHandler() {
  let inputs = document.querySelectorAll('.time-button-group input');
  inputs.forEach((input) => {
    input.addEventListener('click', () => {
      inputTime = input.value;
    });
  });
}

// 예약시간 버튼을 생성하고 초기화하는 함수
function initializeReservationButtons(selectedDate) {
  // 08:00부터 익일 07:30까지 30분 간격으로 버튼 생성
  const startTime = new Date(selectedDate);
  startTime.setHours(0, 0, 0, 0);

  const endTime = new Date(selectedDate);
  endTime.setDate(endTime.getDate() + 1);
  endTime.setHours(23, 30, 0, 0);

  for (let row = 0; row < 6; row++) {
    let div = document.createElement('div');
    div.classList.add('input-group', 'radio-group');
    for (let col = 0; col < 8; col++) {
      const formattedTime = formatTime(startTime);

      let input = document.createElement('input');
      input.classList.add('btn-check',);
      input.type = 'radio';
      input.name = 'time';
      input.value = formattedTime;
      input.id = formattedTime;
      input.disabled = true;

      let label = document.createElement('label');
      label.classList.add('radio', 'btn', 'btn-outline-secondary');
      label.htmlFor = formattedTime;
      label.textContent = formattedTime;

      div.appendChild(input);
      div.appendChild(label);

      startTime.setMinutes(startTime.getMinutes() + 30);
    }
    buttonContainer.appendChild(div);
  }

}

// 요일을 확인하고 활동 가능한 시간과 unavailableTimes에 따라 버튼 상태를 조정하는 함수
function updateReservationButtons(dayOfWeek, activeTimes,
    unavailableTimes, selectedDate) {
  const buttons = document.querySelectorAll('.radio-group input');
  const labels = document.querySelectorAll('.radio-group label');

  let targetActiveTime = activeTimes[dayOfWeek];
  console.log(targetActiveTime);

  targetActiveTime.forEach((activeTimeInfo) => {
    let openTime = activeTimeInfo.open_time;
    let closeTime = activeTimeInfo.close_time;
    if (activeTimeInfo.active_status === 'OPEN') {
      for (let time = openTime; time <= closeTime;
          time = increment30Minutes(time)) {
        buttons.forEach((button) => {
          if (button.id === time) {
            button.disabled = false;
          }
        });

        labels.forEach((label) => {
          if (label.htmlFor === time) {
            label.classList.remove('btn-outline-secondary');
            label.classList.add('btn-outline-success');
          }
        });
      }
    }
  });

  let targetUnavailableTime = unavailableTimes[selectedDate];
  if (targetUnavailableTime !== undefined) {

    targetUnavailableTime.forEach((unavailableTime) => {
      let from = unavailableTime.from;
      console.log(from);
      let to = unavailableTime.to;

      for (let time = decrement1Hours(from); time <= to;
          time = increment30Minutes(time)) {
        buttons.forEach((button) => {
          if (button.id === time) {
            button.disabled = true;
          }
        });

        labels.forEach((label) => {
          if (label.htmlFor === time && label.classList.contains(
              'btn-outline-success')) {
            label.classList.remove('btn-outline-success');
            label.classList.add('btn-outline-secondary');
          }
        });
      }

    });
  }
}

function increment30Minutes(timeString) {
  const [hours, minutes] = timeString.split(':').map(Number);
  const newMinutes = (minutes + 30) % 60;
  const newHours = hours + Math.floor((minutes + 30) / 60);
  const formattedHours = String(newHours).padStart(2, '0');
  const formattedMinutes = String(newMinutes).padStart(2, '0');
  return `${formattedHours}:${formattedMinutes}`;
}

function decrement1Hours(timeString) {
  const [hours, minutes] = timeString.split(':').map(Number);
  const newMinutes = (minutes - 60 + 30) % 60;
  const newHours = hours + Math.floor((minutes - 60 + 30) / 60);
  const formattedHours = String(newHours).padStart(2, '0');
  const formattedMinutes = String(newMinutes).padStart(2, '0');
  return `${formattedHours}:${formattedMinutes}`;
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

//================================Date Parser================================//
function parseDate(date) {
  let year = date.getFullYear();
  let month = String(date.getMonth() + 1).padStart(2, '0');
  let day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function formatTime(dateTime) {
  const hours = dateTime.getHours().toString().padStart(2, '0');
  const minutes = dateTime.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
}

