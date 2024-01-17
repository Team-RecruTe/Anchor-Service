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

// Datepicker에서 날짜 선택시 activeTimes와 UnavailableTimes를 사용해서 시간 버튼 활성화 여부
let elements = document.querySelectorAll('.tui-calendar-date');

const dayOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY',
  'FRIDAY', 'SATURDAY'];

elements.forEach((element) => {
  element.addEventListener('click', () => {
    let selectDate = datepicker.getDate();
    console.log(dayOfWeek[selectDate.getDay()]);
    console.log(parseDate(selectDate));
  });
});

document.addEventListener('DOMContentLoaded', () => {
  let currentUri = window.location.pathname;

  let requestUri = currentUri.replace(/\/apply$/, '/reservation-time');

  axios.get(requestUri)
  .then(res => {
    let data = res.data;
    // console.log(data);
    let activeTimes = data.activeTimes;
    let unavailableTimes = data.unavailableTimes;
    console.log(activeTimes);
    console.log(unavailableTimes);
  })
});

document.getElementById('submitButton').addEventListener('click', () => {
  document.getElementById('form').submit();
  applicationTimeLock();
});

function applicationTimeLock() {
  let currentUri = window.location.pathname;

  let requestUri = currentUri.replace(/\/apply$/, '/lock');

  let inputDate = document.getElementById('datepicker-input').value;
  let inputTime = document.getElementById('time').value;
  let inputDurationTime = document.getElementById('duration-time').value;

  axios.post(requestUri, {
    date: inputDate,
    time: inputTime,
    durationTime: inputDurationTime
  }).catch(error => alert(error));

}

function parseDate(date) {
  let year = date.getFullYear();
  let month = String(date.getMonth() + 1).padStart(2, '0');
  let day = String(date.getDate()).padStart(2, '0');

  return `${year}.${month}.${day}`;
}