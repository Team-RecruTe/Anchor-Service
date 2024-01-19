const editor = new toastui.Editor({
  el: document.querySelector('#content'), // 에디터를 적용할 요소 (컨테이너)
  height: '800px',                        // 에디터 영역의 높이 값 (OOOpx || auto)
  initialEditType: 'markdown',            // 최초로 보여줄 에디터 타입 (markdown || wysiwyg)
  initialValue: '',                       // 내용의 초기 값으로, 반드시 마크다운 문자열 형태여야 함
  previewStyle: 'vertical',               // 마크다운 프리뷰 스타일 (tab || vertical)
  placeholder: '내용을 입력해 주세요.',
  hooks: {
    async addImageBlobHook(blob, callback) { //  (업로드한 이미지를 Blob 형태로 변환, 이미지 업로드 이후 본문에 저장되는 데이터를 지정해주는 콜백함수)
      const formData = new FormData();
      formData.append('image', blob);

      axios.post("/image/upload", formData)
      .then((res) => {
        if (res.status === 200) {
          callback(res.data.imageUrl, blob.name);
          const addedImage = document.querySelector(
              'img[alt="' + blob.name + '"]')
          console.log(addedImage)
          addedImage.id = 'add ' + res.data.imageId;
        }
      })
      .catch((error) => {
        console.error('업로드 중 오류가 발생했습니다.', error);
      });
    },
  }
});

const isUnique = (tags, inputValue) => {
  for (const tag of tags.children) {
    if (tag.textContent === inputValue) {
      return false;
    }
  }
  return true;
}

const addTag = (tagInput, tags) => {
  const inputValue = tagInput.value.trim();
  if (inputValue !== '' && isUnique(tags, inputValue)) {
    const tagSpan = document.createElement('span');
    tagSpan.textContent = inputValue;
    tagSpan.classList.add('tag-' + tags.children.length)
    tagSpan.classList.add('me-2')
    tags.appendChild(tagSpan);
    tagInput.value = '';
  } else if (inputValue === '') {
    tagInput.value = '';
    tagInput.placeholder = '값을 입력해주세요.'
  } else {
    tagInput.value = '';
    tagInput.placeholder = '중복되는 태그입니다.'
  }
}

const tagEvent = (tags, tagInput, addButton, addTag) => {
  // Enter 키 이벤트 처리
  tagInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && e.isComposing === false) {
      addTag(tagInput, tags);
    }
  });

  // 버튼 클릭 이벤트 처리
  addButton.addEventListener('click', () => {
    addTag(tagInput, tags)
  });
}

const submit = (axios, editor, tags) => {
  console.log(editor.getHTML())
  const addedImages = document.querySelectorAll('img[id*="add"]')
  console.log(addedImages)
  alert("alert")
  const imageIds = Array.from(addedImages).map(image => image.id.split(" ")[1])
  const tagList = Array.from(tags.children).map(tag => tag.textContent)
  const json = {
    contents: editor.getHTML(),
    tags: tagList,
    imageIds: imageIds,
  }
  const currentPath = window.location.pathname
  const suffix = '/edit'
  const path = currentPath.substring(0, currentPath.indexOf(suffix))

  axios.put(path, json)
  .then((res) => {
    if (res.status === 200) {
      for (const link of res.data.links) {
        if (link.rel === "self") {
          window.location.href = link.href
          return
        }
      }
      window.location.href = "/"
    } else {
      alert('url이 반환되지 않았습니다.')
    }
  })
  .catch((error) => {
    console.error('PUT 요청 중 오류가 발생했습니다.', error);
    alert('PUT 요청 중에 문제가 발생했습니다.')
  });
}

const submitEvent = (axios, submitButton, editor, tags, submit) => {
  submitButton.addEventListener('click',
      () => submit(axios, editor, tags));
}
