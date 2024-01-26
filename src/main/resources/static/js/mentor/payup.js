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
let visitedMonths = [];

function handleCalendarChange() {
  currentMonthRange(calendar);
  let thisMonth = calendar.getDate().toDate();
  let startMonth = getSixMonthAgo(thisMonth);
  const newMonth = getFormattedMonth(thisMonth);
  if (visitedMonths.length === 0) {
    generateMonthsList(startMonth, thisMonth);
    getPayupInfos(startMonth, thisMonth);
  }
  if (!visitedMonths.includes(newMonth)) {
    generateMonthsList(startMonth, thisMonth);
    getPayupInfos(startMonth, thisMonth);
  }
  console.log(visitedMonths);
}

function getSixMonthAgo(date) {
  return new Date(date.getFullYear(), date.getMonth() - 5, date.getDate());
}

function generateMonthsList(startMonth, endMonth) {

  let currentDate = new Date(startMonth);

  while (currentDate <= endMonth) {
    visitedMonths.push(getFormattedMonth(currentDate));
    currentDate.setMonth(currentDate.getMonth() + 1);
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

function getPayupInfos(startMonth, thisMonth) {
  let today = parseDateTime(thisMonth);
  let startDay = parseDateTime(startMonth);
  let events = [];
  axios.get('/mentors/me/payup-info', {
    params: {
      currentMonth: today,
      startMonth: startDay
    }
  })
  .then(res => {
    let data = res.data;
    scheduleMap.set(parseYearMonth(thisMonth), data.dailyMentoringPayupInfo);
    // 정산된 멘토링 정보가 담길 Array
    let dailyTotalAmountMap = data.dailyTotalAmount;
    if (dailyTotalAmountMap !== null) {
      Object.entries(dailyTotalAmountMap).forEach(([day, totalAmount]) => {
        Object.values(totalAmount).forEach((map) => {
          Object.entries(map).forEach(([status, amount]) => {
            events.push({
              id: `${day}_${status}`,
              calendarId: `${day}_${status}`,
              title: `${status === 'COMPLETE' ? '정산' : '대기'}: ${amount}` + '원',
              body: '',
              isAllday: true,
              category: 'allday',
              start: day.replace(/T.*/, ""),
              end: day.replace(/T.*/, ""),
              backgroundColor: `${status === 'COMPLETE' ? '#abf7da' : '#a796eb'}`
            });
          });
        });
      });
    }
    calendar.createEvents(events);
  });

}

// 일정 클릭시 모달 생성
let modalElement = document.getElementById('myModal');
let modalTitleElement = document.getElementById('modal-title');
let tableBody = document.getElementById('payup-table-body');

calendar.on('clickEvent', (e) => {
  let id = parseId(e.event.id);

  modalTitleElement.innerHTML = parseDate(id);
  let schedule = scheduleMap.get(parseYearMonth(id));
  schedule[id].forEach((element) => {
    let fromDate = parseTime(element.dateTimeRange.from);
    let toDate = parseTime(element.dateTimeRange.to);

    tableBody.innerHTML += `<tr>
                      <td>${fromDate} ~ ${toDate}</td>
                      <td>${element.menteeNickname}</td>
                      <td>${element.payupStatus === 'COMPLETE' ? '정산완료' : '정산대기'}</td>
                      <td>${element.paymentAmount}원</td>
                      <td>${element.payupAmount}원</td>
                        </tr>`;

  });
  document.querySelectorAll('th').forEach(th => th.style.textAlign = 'center');
  document.querySelectorAll('td').forEach(td => td.style.textAlign = 'center');

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
  let currentMonth = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
  let navbar = document.querySelector('.navbar--range');
  navbar.innerHTML = currentMonth;
}

// event ID parser
function parseId(id) {
  let COMPLETE = '_COMPLETE';
  let WAITING = '_WAITING';
  if (id.includes(COMPLETE)) {
    return id.replace(COMPLETE, '');
  }
  if (id.includes(WAITING)) {
    return id.replace(WAITING, '');
  }
}

//=================================Date 객체 파싱함수=========================================//
function getFormattedMonth(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
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

