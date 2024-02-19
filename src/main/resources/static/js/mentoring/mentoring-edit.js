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
    const tagSpan = tag.querySelector('span');
    if (tagSpan && tagSpan.textContent === inputValue) {
      return false;
    }
  }
  return true;
}

const addTag = (tagInput, tags) => {
  const inputValue = tagInput.value.trim();
  if (inputValue !== '' && isUnique(tags, inputValue)) {
    tagInput.placeholder = '태그를 입력하세요.'
    const delBtn = document.createElement('button')
    delBtn.classList.add('tag-del-btn')
    delBtn.classList.add('me-2')
    const tagh5 = document.createElement('h5')
    tagh5.classList.add('h-tag')
    tagh5.classList.add('h-tag-' + tags.children.length)
    tagh5.classList.add('d-flex')
    tagh5.classList.add('align-items-center')
    const tagSpan = document.createElement('span');
    tagSpan.textContent = inputValue;
    tagSpan.classList.add('tag-' + tags.children.length)
    tagSpan.classList.add('badge')
    tagSpan.classList.add('bg-success')
    tagh5.appendChild(tagSpan)
    tagh5.appendChild(delBtn)
    tags.appendChild(tagh5);
    tagInput.value = '';

    delBtn.addEventListener('click', e => {
      tagh5.parentNode.removeChild(tagh5);
    })

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

const submit = (axios, title, durationTime, cost, editor, tags) => {
  console.log(editor.getHTML())
  const addedImages = document.querySelectorAll('img[id*="add"]')
  console.log(addedImages)
  const imageIds = Array.from(addedImages).map(image => image.id.split(" ")[1])
  const tagList = Array.from(tags.children).map(
      tag => tag.querySelector('span')).filter(span => span !== null).map(span => span.textContent)

  const basic = {
    title: title.value,
    durationTime: durationTime.value,
    cost: cost.value
  }

  const contents = {
    contents: editor.getHTML(),
    tags: tagList,
    imageIds: imageIds
  }

  const currentPath = window.location.pathname
  const suffix = '/edit'
  const path = currentPath.substring(0, currentPath.indexOf(suffix))

  axios.put(path, basic).then(res => {
    if (res.status === 200) {
      axios.put(`${path}/contents`, contents).then(res => {
        window.location = `/mentorings/${res.data.id}`
      }).catch(e => {
        alert("멘토링 내용 생성에 실패하였습니다.")
        console.log(e)
      })
    }
  }).catch(e => {
    alert("멘토링 생성에 실패하였습니다.")
    console.log(e)
  })
}

const submitEvent = (axios, submitButton, title, durationTime, cost, editor,
    tags, submit) => {
  submitButton.addEventListener('click',
      () => submit(axios, title, durationTime, cost, editor, tags));
}
