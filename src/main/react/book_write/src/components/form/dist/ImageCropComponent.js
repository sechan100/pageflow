"use strict";
exports.__esModule = true;
var react_1 = require("react");
var react_image_crop_1 = require("react-image-crop");
require("react-image-crop/dist/ReactCrop.css");
function ImageCropComponent(_a) {
    var defaultSrc = _a.defaultSrc, cropedImgFile = _a.cropedImgFile;
    // 원본 이미지 src: 최초 props로 받아온게 없다면 default 이미지로 설정
    var _b = react_1.useState(defaultSrc !== undefined ? defaultSrc : "/img/defaultImg.jpg"), src = _b[0], setSrc = _b[1];
    // 크롭된 이미지 src
    var _c = react_1.useState(null), croppedImageUrl = _c[0], setCroppedImageUrl = _c[1];
    // 이미지 ref
    var imageRef = react_1.useRef(null);
    // 저장하기 버튼 ref
    var saveCropStatusBtn = react_1.useRef(null);
    // 수정하기 버튼 ref
    var modifyCropStatusBtn = react_1.useRef(null);
    // 이미지 드롭 or 선택 input ref
    var sourceImgInput = react_1.useRef(null);
    // 크롭 상태
    var _d = react_1.useState({
        x: 0,
        y: 0,
        width: 11 * 3.5,
        height: 16 * 3.5,
        unit: '%'
    }), crop = _d[0], setCrop = _d[1];
    var cropedImgData = react_1.useRef({
        savedCrop: crop
    });
    return (react_1["default"].createElement("div", null,
        src && (react_1["default"].createElement(react_image_crop_1["default"], { crop: crop, onChange: onCropChange, aspect: 11 / 16 },
            react_1["default"].createElement("div", { className: 'w-auto overflow-hidden border border-4 border-blue-500' },
                react_1["default"].createElement("img", { src: croppedImageUrl !== null ? croppedImageUrl : src, ref: imageRef, alt: "Crop me" })))),
        react_1["default"].createElement("div", { className: "flex items-center justify-between items-stretch w-full" },
            react_1["default"].createElement("label", { htmlFor: "coverImgDropzone", className: "flex flex-col items-center justify-center w-full h-28 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:hover:bg-bray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600" },
                react_1["default"].createElement("div", { className: "flex flex-col items-center justify-center pt-5 pb-6" },
                    react_1["default"].createElement("svg", { className: "w-8 h-8 mb-4 text-gray-500 dark:text-gray-400", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 20 16" },
                        react_1["default"].createElement("path", { stroke: "currentColor", "stroke-linecap": "round", "stroke-linejoin": "round", "stroke-width": "2", d: "M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2" })),
                    react_1["default"].createElement("p", { className: "mb-2 text-sm text-gray-500 dark:text-gray-400" },
                        react_1["default"].createElement("span", { className: "font-semibold" }, "\uD074\uB9AD\uD558\uAC70\uB098 "),
                        "\uB4DC\uB798\uADF8\uD558\uC5EC \uC0AC\uC9C4\uC744 \uC5C5\uB85C\uB4DC"),
                    react_1["default"].createElement("p", { className: "text-xs text-gray-500 dark:text-gray-400" }, "11 x 16 \uBE44\uC728")),
                react_1["default"].createElement("input", { id: "coverImgDropzone", ref: sourceImgInput, accept: 'image/*', onChange: onSelectFile, type: "file", className: "hidden" })),
            react_1["default"].createElement("button", { type: 'button', ref: saveCropStatusBtn, onClick: saveCropStatus, className: 'hidden px-4 ml-5 flex-none text-white bg-blue-500 rounded-lg hover:bg-blue-600' }, "\uC800\uC7A5\uD558\uAE30"),
            react_1["default"].createElement("button", { type: 'button', ref: modifyCropStatusBtn, onClick: modifyCropStatus, className: 'hidden px-4 ml-5 flex-none text-white bg-blue-500 rounded-lg hover:bg-blue-600' }, "\uC218\uC815\uD558\uAE30"))));
    function onSelectFile(e) {
        var _a;
        if (e.target.files && e.target.files.length > 0) {
            var reader_1 = new FileReader();
            reader_1.addEventListener('load', function () { return setSrc(reader_1.result); });
            reader_1.readAsDataURL(e.target.files[0]);
        }
        (_a = saveCropStatusBtn.current) === null || _a === void 0 ? void 0 : _a.classList.toggle('hidden');
    }
    ;
    function onCropChange(newCrop) {
        setCrop(newCrop);
    }
    ;
    function saveCropStatus() {
        var _a, _b;
        if (imageRef.current && crop.width && crop.height) {
            var croppedImageUrl_1 = getCroppedImg(imageRef.current, crop);
            setCroppedImageUrl(croppedImageUrl_1);
            cropedImgData.current.savedCrop = crop;
            setCrop({
                x: 0,
                y: 0,
                width: 0,
                height: 0,
                unit: '%'
            });
            (_a = saveCropStatusBtn.current) === null || _a === void 0 ? void 0 : _a.classList.toggle('hidden');
            (_b = modifyCropStatusBtn.current) === null || _b === void 0 ? void 0 : _b.classList.toggle('hidden');
            setCropedCoverImg(dataURLtoFile(croppedImageUrl_1, 'coverImg.jpg'));
        }
    }
    ;
    function modifyCropStatus() {
        var _a, _b;
        setCroppedImageUrl(null);
        setCrop(cropedImgData.current.savedCrop);
        (_a = saveCropStatusBtn.current) === null || _a === void 0 ? void 0 : _a.classList.toggle('hidden');
        (_b = modifyCropStatusBtn.current) === null || _b === void 0 ? void 0 : _b.classList.toggle('hidden');
    }
    function getCroppedImg(image, crop) {
        var canvas = document.createElement('canvas');
        var scaleX = image.naturalWidth / image.width;
        var scaleY = image.naturalHeight / image.height;
        // 캔버스의 크기를 원본 이미지의 크기로 설정
        canvas.width = image.naturalWidth;
        canvas.height = image.naturalHeight;
        var ctx = canvas.getContext('2d');
        if (!ctx) {
            return '';
        }
        // 원본 이미지를 캔버스에 그림
        ctx.drawImage(image, 0, 0, image.naturalWidth, image.naturalHeight);
        // 어두운 오버레이를 추가하기 위한 설정
        ctx.fillStyle = 'rgba(0, 0, 0, 0.8)'; // 반투명 검은색
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        // 크롭 영역 내에서만 원본 이미지를 그려넣음 (어두운 오버레이를 '지움')
        ctx.globalCompositeOperation = 'destination-out';
        ctx.drawImage(image, crop.x * scaleX, crop.y * scaleY, crop.width * scaleX, crop.height * scaleY, crop.x * scaleX, crop.y * scaleY, crop.width * scaleX, crop.height * scaleY);
        // 크롭 영역 내 이미지를 복원
        ctx.globalCompositeOperation = 'source-over';
        ctx.drawImage(image, crop.x * scaleX, crop.y * scaleY, crop.width * scaleX, crop.height * scaleY, crop.x * scaleX, crop.y * scaleY, crop.width * scaleX, crop.height * scaleY);
        // 크롭된 이미지를 data URL 형태로 반환
        return canvas.toDataURL('image/jpeg');
    }
    ;
    function dataURLtoFile(dataUrl, filename) {
        // dataURL의 내용을 분리하여 MIME 타입과 데이터 부분을 추출합니다.
        var arr = dataUrl.split(','), mime = arr[0].match(/:(.*?);/)[1], bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
        // Uint8Array를 사용하여 바이너리 데이터를 생성합니다.
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        // Blob 객체를 생성하여 파일로 반환합니다.
        return new File([u8arr], filename, { type: mime });
    }
}
exports["default"] = ImageCropComponent;
