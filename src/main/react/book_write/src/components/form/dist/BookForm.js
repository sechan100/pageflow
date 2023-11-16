"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
exports.__esModule = true;
/* eslint-disable react-hooks/exhaustive-deps */
var react_1 = require("react");
var book_apis_1 = require("../../api/book-apis");
var SaveMutationBtn_1 = require("./SaveMutationBtn");
var book_apis_2 = require("../../api/book-apis");
var ImageCropComponent_1 = require("./ImageCropComponent");
function BookForm(props) {
    var bookId = props.bookId, queryClient = props.queryClient;
    var outline = book_apis_1.useGetOutline(bookId);
    var isUpdated = react_1.useRef(false); // 실제로 데이터가 업데이트 되었는지를 기록, 불필요한 서버 통신을 사전에 막는다.
    var _a = react_1.useState(false), saveActive = _a[0], setSaveActive = _a[1]; // Save 버튼의 클릭 상태를 상위 컴포넌트로 끌어올리기 위한 state
    var coverImgPreview = react_1.useRef(null); // coverImg의 미리보기를 위한 ref
    var coverImgInput = react_1.useRef(null); // coverImg의 input을 위한 ref
    var _b = react_1.useState({
        title: outline.title,
        coverImg: null
    }), bookMutation = _b[0], setBookMutation = _b[1];
    var _c = book_apis_2.useUpdateBook(bookId), mutateAsync = _c.mutateAsync, isLoading = _c.isLoading, error = _c.error;
    // outline 데이터의 변경시, 이미 선언된 state인 bookMutation의 상태를 업데이트하기 위함
    react_1.useEffect(function () {
        setBookMutation({
            title: outline.title,
            coverImg: null
        });
    }, [outline]);
    react_1.useEffect(function () {
        // 업데이트가 되었다면 서버에 요청을 보낸다.
        if (isUpdated.current) {
            mutateAsync(bookMutation);
            isUpdated.current = false; // 초기화
        }
    }, [saveActive]);
    return (React.createElement(React.Fragment, null,
        React.createElement(SaveMutationBtn_1["default"], { setSaveActive: setSaveActive, isUpdated: isUpdated }),
        React.createElement("div", { className: "px-24 mt-16" },
            React.createElement("div", { className: "sm:col-span-2" },
                React.createElement("label", { htmlFor: "title", className: "block mb-2 text-sm font-medium text-gray-900" }, "\uCC45 \uCCB4\uBAA9"),
                React.createElement("input", { value: bookMutation.title, onChange: handleBookTitle, onKeyDown: handleTitleInputEnterPress, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })),
            React.createElement(ImageCropComponent_1["default"], null))));
    function handleBookTitle(e) {
        setBookMutation(function (prev) { return (__assign(__assign({}, prev), { title: e.target.value })); });
        // 기존 업데이트 전의 데이터와 비교하여 다르다면 isUpdated를 true로 변경
        if (e.target.value !== outline.title) {
            isUpdated.current = true;
            // 중간에 변경되었더라도, 다시 원래대로 돌아온 경우, isUpdated를 false로 변경  
        }
        else {
            isUpdated.current = false;
        }
    }
    function handleTitleInputEnterPress(e) {
        if (e.key === 'Enter' && isUpdated.current) {
            setSaveActive(function (prev) { return (!prev); });
        }
    }
    function readAndShowCoverImgFile() {
        var _a, _b;
        var preview = coverImgPreview.current;
        if (!preview)
            return;
        var coverImg = (_b = (_a = coverImgInput.current) === null || _a === void 0 ? void 0 : _a.files) === null || _b === void 0 ? void 0 : _b[0];
        var reader = new FileReader();
        reader.onloadend = function () {
            preview.src = reader.result;
        };
        if (coverImg) {
            reader.readAsDataURL(coverImg);
        }
        else {
            preview.classList.add("hidden");
        }
    }
}
exports["default"] = BookForm;
