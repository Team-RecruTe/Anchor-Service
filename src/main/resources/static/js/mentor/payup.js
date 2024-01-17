// 페이지 로드가 완료될 시 이번달 정산내역 조회
window.onload = function () {
  handleCalendarChange();
}

// toast ui calendar 초기화
const tuiCalendar = tui.Calendar
const calendarContainer = document.querySelector('#calendar')
const calendar = new tuiCalendar(calendarContainer, {
  defaultView: 'month',
  isReadOnly: true,
  timezone: {
    zones: [
      {
        timezoneName: 'Asia/Seoul',
        displayLabel: 'Seoul',
      },
    ]
  },
  usageStatistics: false
});

// 캘린더 이동을 체크하는 함수
// 한번 조회한 달이라면 조회요청을 하지 않는다.
let processedMonths = [];

function handleCalendarChange() {
  currentMonthRange(calendar);
  const newMonth = getFormattedMonth(calendar.getDate().toDate());

  if (!processedMonths.includes(newMonth)) {
    getPayupInfos(calendar);
    processedMonths.push(newMonth);
  }
}

// 이전달, 이번달, 오늘 버튼
const prevButton = document.querySelector('.prev');
const nextButton = document.querySelector('.next');
const todayButton = document.querySelector('.today');

prevButton.addEventListener('click', function () {
  calendar.prev();
  handleCalendarChange();
});
nextButton.addEventListener('click', function () {
  calendar.next();
  handleCalendarChange();
});
todayButton.addEventListener('click', function () {
  calendar.today();
  handleCalendarChange();
});

// 날짜 '2024-01-03' 가 key 인 Map
// value : Array 해당일자에 진행한 멘토링 중 정산된 멘토링 내역의 정보 (시간, 멘티 닉네임, 멘토링 제목, 금액)
let scheduleMap = new Map();

function getPayupInfos(calendar) {
  let todayDate = calendar.getDate().toDate();
  let today = parseDateTime(todayDate);
  let events = [];
  axios.get('/mentors/me/payup-info', {
    params: {
      currentMonth: today
    }
  })
  .then(res => {
    let data = res.data;
    scheduleMap.set(parseYearMonth(todayDate), data.dailyMentoringPayupInfo);
    // 정산된 멘토링 정보가 담길 Array
    let dayAmount = data.dailyTotalAmount;
    if (dayAmount !== null) {
      for (const [day, amount] of Object.entries(dayAmount)) {
        const event = {
          id: day,
          calendarId: day,
          title: `${amount}` + '원',
          body: '',
          isAllday: true,
          category: 'allday',
          start: day.replace(/T.*/, ""),
          end: day.replace(/T.*/, ""),
          backgroundColor: '#abf7da'
        };
        events.push(event);
      }
    }
    calendar.createEvents(events);
  });

}

// 일정 클릭시 모달 생성
let modalElement = document.getElementById('myModal');
let modalTitleElement = document.getElementById('modal-title');
let tableBody = document.getElementById('payup-table-body');

calendar.on('clickEvent', (e) => {
  let id = e.event.id;
  modalTitleElement.innerHTML = parseDate(id);
  let schedule = scheduleMap.get(parseYearMonth(id));
  for (const element of schedule[id]) {

    let fromDate = parseTime(element.dateTimeRange.from);
    let toDate = parseTime(element.dateTimeRange.to);

    tableBody.innerHTML += `<tr>
                      <td>${fromDate} ~ ${toDate}</td>
                      <td>${element.menteeNickname}</td>
                      <td>${element.payupAmount}원</td>
                        </tr>`;
  }

  let modal = new bootstrap.Modal(modalElement);
  modal.show();
});

// modal 사라질때 table 내부 html 삭제
modalElement.addEventListener('hidden.bs.modal', () => {
  tableBody.innerHTML = '';
});

// 현재 캘린더가 보여주는 달(Month) 출력
function currentMonthRange(calendar) {
  let date = calendar.getDate();
  let currentMonth = `${date.getFullYear()}-${(date.getMonth()
      + 1).toString().padStart(2, '0')}`;
  let navbar = document.querySelector('.navbar--range');
  navbar.innerHTML = currentMonth;
}

//=================================Date 객체 파싱함수=========================================//
function getFormattedMonth(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2,
      '0')}`;
}

function parseDateTime(date) {
  return date.toISOString().slice(0, -5);
}

function parseYearMonth(stringDate) {
  let date = new Date(stringDate);
  let year = date.getFullYear();
  let month = String(date.getMonth() + 1).padStart(2, '0');
  return `${year}-${month}`;
}

function parseDate(stringDate) {
  let date = new Date(stringDate);
  let year = date.getFullYear();
  let month = String(date.getMonth() + 1).padStart(2, '0');
  let day = String(date.getDate()).padStart(2, '0');

  return `${year}.${month}.${day}`;
}

function parseTime(stringDate) {
  let date = new Date(stringDate);
  let hours = String(date.getHours()).padStart(2, '0');
  let minutes = String(date.getMinutes()).padStart(2, '0');
  return `${hours}:${minutes}`;
}

