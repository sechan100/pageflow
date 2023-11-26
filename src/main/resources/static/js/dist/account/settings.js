"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("axios");
// 프로필 정보 업데이트
function updateProfile(e) {
    e.preventDefault();
    var formData = new FormData(e);
    axios_1.default.post('/api/account/settings/profile', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
        .then(function (res) {
        console.log(res);
    })
        .catch(function (err) {
        console.log(err.response);
    });
}
