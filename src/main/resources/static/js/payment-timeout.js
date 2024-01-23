let loginButton = document.getElementById('header-login');
if (loginButton !== null) {
  loginButton.addEventListener('click', () => {
    location.href = '/login';
  });
}

setInterval(paymentPageValidation, 270_000);

function paymentPageValidation() {
  let currentUri = window.location.pathname;
  let isMatch = currentUriMatcher(currentUri);

  if (isMatch) {
    let confirmFlag = confirm('결제페이지를 유지하시겠습니까?');

    if (confirmFlag) {
      let requestUri = currentUri.replace(/\/payment$/, '/refresh');
      axios.put(requestUri)
      .then(res => {
        if (res.status !== 200) {
          alert(res.data);
          history.back();
        }
      });
    }

    if (!confirmFlag) {
      unlock(currentUri);
    }
  }
}

function currentUriMatcher(currentUri) {
  let uriPattern = /^\/mentorings\/\d+\/payment$/;

  return uriPattern.test(currentUri);
}

function unlock(currentUri) {
  let requestUri = currentUri.replace(/\/payment$/, '/lock');
  axios.delete(requestUri).then(res => location.href = '/');
}