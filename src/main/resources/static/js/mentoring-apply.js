document.addEventListener('DOMContentLoaded', () => {
  let currentUri = window.location.pathname;

  let requestUri = currentUri.replace(/\/apply$/, '/reservation-time');

  axios.get(requestUri)
  .then(res => console.log(res.data));
});

document.getElementById('submitButton').addEventListener('click', () => {
  document.getElementById('form').submit();
  applicationTimeLock();
});

function applicationTimeLock() {
  let currentUri = window.location.pathname;

  let requestUri = currentUri.replace(/\/apply$/, '/lock');

  let inputDate = document.getElementById('date').value;
  let inputTime = document.getElementById('time').value;
  let inputDurationTime = document.getElementById('duration-time').value;

  axios.post(requestUri, {
    date: inputDate,
    time: inputTime,
    durationTime: inputDurationTime
  }).catch(error => alert(error));

}