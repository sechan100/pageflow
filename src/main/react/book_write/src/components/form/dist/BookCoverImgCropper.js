"use strict";
exports.__esModule = true;
var react_1 = require("react");
var react_image_crop_1 = require("react-image-crop");
require("react-image-crop/dist/ReactCrop.css");
function ImageCropComponent(_a) {
    var cropedFilename = _a.cropedFilename, defaultSrc = _a.defaultSrc, setFileDate = _a.setFileDate;
    // 원본 이미지 src: 최초 props로 받아온게 없다면 default 이미지로 설정
    var _b = react_1.useState(defaultSrc !== undefined ? defaultSrc : "/img/unloaded_img.jpg"), src = _b[0], setSrc = _b[1];
    // props로 받아온 defaultSrc가 변경되면 src도 변경
    react_1.useEffect(function () {
        setSrc(defaultSrc !== undefined ? defaultSrc : "/img/unloaded_img.jpg");
    }, [defaultSrc]);
    // 크롭된 이미지 src
    var _c = react_1.useState(null), croppedImageSrc = _c[0], setCroppedImageSrc = _c[1];
    // 이미지 ref
    var imageRef = react_1.useRef(null);
    // 수정상태 여부
    var _d = react_1.useState(false), isModifyMode = _d[0], setIsModifyMode = _d[1];
    // 이미지 드롭 or 선택 input ref
    var sourceImgInput = react_1.useRef(null);
    // 크롭 상태
    var _e = react_1.useState({
        x: 0,
        y: 0,
        width: 200 * 11 / 16,
        height: 200,
        unit: 'px'
    }), crop = _e[0], setCrop = _e[1];
    var cropedImgData = react_1.useRef({
        savedCrop: crop
    });
    return (react_1["default"].createElement("div", null,
        isModifyMode &&
            react_1["default"].createElement("div", { className: 'flex justify-center border border-4 border-blue-500 p-5 bg-gray-100' },
                react_1["default"].createElement(react_image_crop_1["default"], { crop: crop, onChange: onCropChange, aspect: 11 / 16, ruleOfThirds: true },
                    react_1["default"].createElement("div", { className: 'w-auto overflow-hidden' },
                        react_1["default"].createElement("img", { src: croppedImageSrc !== null ? croppedImageSrc : src, ref: imageRef, alt: "Crop me" })))),
        !isModifyMode &&
            react_1["default"].createElement("div", { className: 'w-auto flex justify-center overflow-hidden border border-4 border-blue-500 p-5 bg-gray-100' },
                react_1["default"].createElement("img", { src: croppedImageSrc !== null ? croppedImageSrc : src, ref: imageRef, alt: "Crop me" })),
        react_1["default"].createElement("div", { className: "flex items-center justify-between items-stretch w-full h-28 mt-1" },
            isModifyMode &&
                react_1["default"].createElement("label", { htmlFor: "coverImgDropzone", className: "flex flex-col mr-5 items-center justify-center w-full h-28 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:hover:bg-bray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600" },
                    react_1["default"].createElement("div", { className: "flex flex-col items-center justify-center pt-5 pb-6" },
                        react_1["default"].createElement("svg", { className: "w-8 h-8 mb-4 text-gray-500 dark:text-gray-400", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 20 16" },
                            react_1["default"].createElement("path", { stroke: "currentColor", strokeLinecap: "round", strokeLinejoin: "round", strokeWidth: "2", d: "M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2" })),
                        react_1["default"].createElement("p", { className: "mb-2 text-sm text-gray-500 dark:text-gray-400" },
                            react_1["default"].createElement("span", { className: "font-semibold" }, "\uD074\uB9AD\uD558\uAC70\uB098 "),
                            "\uB4DC\uB798\uADF8\uD558\uC5EC \uC0AC\uC9C4\uC744 \uC5C5\uB85C\uB4DC"),
                        react_1["default"].createElement("p", { className: "text-xs text-gray-500 dark:text-gray-400" }, "11 x 16 \uBE44\uC728")),
                    react_1["default"].createElement("input", { id: "coverImgDropzone", ref: sourceImgInput, accept: 'image/*', onChange: onSelectFile, type: "file", className: "hidden" })),
            isModifyMode && react_1["default"].createElement("button", { type: 'button', onClick: saveCropStatus, className: 'px-4 flex-initial w-full text-white bg-blue-500 rounded-lg hover:bg-blue-600' }, "\uC800\uC7A5\uD558\uAE30"),
            !isModifyMode && react_1["default"].createElement("button", { type: 'button', onClick: modifyCropStatus, className: 'px-4 flex-initial w-full text-white bg-blue-500 rounded-lg hover:bg-blue-600' }, "\uC218\uC815\uD558\uAE30"))));
    // 이미지 드롭 or 선택시
    function onSelectFile(e) {
        if (e.target.files && e.target.files.length > 0) {
            var reader_1 = new FileReader();
            reader_1.addEventListener('load', function () { return setSrc(reader_1.result); });
            reader_1.readAsDataURL(e.target.files[0]);
        }
    }
    ;
    // 크롭 상태 변경시
    function onCropChange(newCrop) {
        setCrop(newCrop);
    }
    ;
    // 저장하기 버튼 클릭시
    function saveCropStatus() {
        if (imageRef.current && crop.width && crop.height) {
            // [크롭 영역이 아닌 곳은 어두운 오버레이가 적용된 사진, 크롭 영역만 포함한 사진]
            var croppedImageSrc_1 = getCroppedImg(imageRef.current, crop);
            setCroppedImageSrc(croppedImageSrc_1);
            cropedImgData.current.savedCrop = crop;
            setCrop({
                x: 0,
                y: 0,
                width: 0,
                height: 0,
                unit: '%'
            });
            setIsModifyMode(function (prev) { return !prev; });
            var cropedCoverImg = dataURLtoFile(croppedImageSrc_1);
            setFileDate({ type: "COVER_IMG", payload: cropedCoverImg });
        }
    }
    ;
    // 수정하기 버튼 클릭시
    function modifyCropStatus() {
        setCroppedImageSrc(null);
        setCrop(cropedImgData.current.savedCrop);
        setIsModifyMode(function (prev) { return !prev; });
    }
    // 이미지 크롭 함수
    function getCroppedImg(image, crop) {
        var canvas = document.createElement('canvas');
        var scaleX = image.naturalWidth / image.width;
        var scaleY = image.naturalHeight / image.height;
        canvas.width = crop.width;
        canvas.height = crop.height;
        var ctx = canvas.getContext('2d');
        ctx === null || ctx === void 0 ? void 0 : ctx.drawImage(image, crop.x * scaleX, crop.y * scaleY, crop.width * scaleX, crop.height * scaleY, 0, 0, crop.width, crop.height);
        return canvas.toDataURL('image/jpeg');
    }
    ;
    // dataURL을 File 객체로 변환
    function dataURLtoFile(dataUrl) {
        // dataURL의 내용을 분리하여 MIME 타입과 데이터 부분을 추출합니다.
        var arr = dataUrl.split(",");
        var mime = arr[0].match(/:(.*?);/)[1];
        var bstr = atob(arr[1]);
        var n = bstr.length;
        var u8arr = new Uint8Array(n);
        // Uint8Array를 사용하여 바이너리 데이터를 생성합니다.
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        // Blob 객체를 생성하여 파일로 반환합니다.
        return new File([u8arr], cropedFilename + ".jpeg", { type: mime });
    }
}
exports["default"] = ImageCropComponent;
