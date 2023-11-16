"use strict";
exports.__esModule = true;
function ImageCropComponent() {
    import React, { useState } from 'react';
    import ReactCrop from 'react-image-crop';
    function ImageCropComponent() {
        var _a = react_1.useState(null), src = _a[0], setSrc = _a[1];
        var _b = react_1.useState({ aspect: 11 / 16 }), crop = _b[0], setCrop = _b[1];
        var onImageLoaded = function (image) {
            // 이미지 로드 완료 후 처리
        };
        var onCropChange = function (crop, percentCrop) {
            // 크롭 영역 변경 시 처리
            setCrop(crop);
        };
        var onCropComplete = function (crop, percentCrop) {
            // 크롭 작업 완료 후 처리
        };
        var onSelectFile = function (e) {
            if (e.target.files && e.target.files.length > 0) {
                var reader_1 = new FileReader();
                reader_1.addEventListener('load', function () {
                    if (typeof reader_1.result === 'string') {
                        setSrc(reader_1.result);
                    }
                });
                reader_1.readAsDataURL(e.target.files[0]);
            }
        };
        return (react_1["default"].createElement("div", null,
            react_1["default"].createElement("input", { type: "file", accept: "image/*", onChange: onSelectFile }),
            src && (react_1["default"].createElement(react_image_crop_1["default"], { src: src, crop: crop, onImageLoaded: onImageLoaded, onComplete: onCropComplete, onChange: onCropChange }))));
    }
    export default ImageCropComponent;
}
;
return (react_2["default"].createElement("div", null,
    react_2["default"].createElement("input", { type: "file", accept: "image/*", onChange: onSelectFile }),
    src && (react_2["default"].createElement(react_image_crop_2["default"], { src: src, crop: crop, onImageLoaded: onImageLoaded, onComplete: onCropComplete, onChange: onCropChange }))));
exports["default"] = ImageCropComponent;
