import axios from 'axios';



// 프로필 정보 업데이트
function updateProfile(e : HTMLFormElement) {

    e.preventDefault();

    const formData = new FormData(e);

    axios.post('/api/account/settings/profile', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
            }
        })
        .then((res) => {
            console.log(res);
        })
        .catch((err) => {
            console.log(err.response);
        })
}