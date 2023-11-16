"use strict";
/* eslint-disable react-hooks/exhaustive-deps */
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
var react_1 = require("react");
var App_1 = require("../../../App");
var outline_api_1 = require("../../../api/outline-api");
var book_api_1 = require("../../../api/book-api");
var MutationSaveBtn_1 = require("../MutationSaveBtn");
var BookCoverImgCropper_1 = require("../BookCoverImgCropper");
var flowAlert_1 = require("../../../etc/flowAlert");
function BookForm() {
    var bookId = react_1.useContext(App_1.QueryContext).bookId;
    var outline = outline_api_1.useGetOutlineQuery(bookId);
    var isUpdated = react_1.useRef(false); // 실제로 데이터가 업데이트 되었는지를 기록, 불필요한 서버 통신을 사전에 막는다.
    var _a = react_1.useState(false), saveActive = _a[0], setSaveActive = _a[1]; // Save 버튼의 클릭 상태를 상위 컴포넌트로 끌어올리기 위한 state
    var _b = react_1.useState(null), coverImg = _b[0], setCoverImg = _b[1]; // coverImg의 파일을 저장하기 위한 state
    var _c = react_1.useState({
        title: outline.title,
        coverImg: null
    }), bookMutation = _c[0], setBookMutation = _c[1];
    var _d = book_api_1.useUpdateBook(bookId), mutateAsync = _d[0], isLoading = _d[1], isError = _d[2];
    // outline 데이터의 변경시, 이미 선언된 state인 bookMutation의 상태를 업데이트하기 위함
    react_1.useEffect(function () {
        if (outline) {
            setBookMutation({
                title: outline.title,
                coverImg: null
            });
        }
    }, [outline]);
    react_1.useEffect(function () {
        if (coverImg !== null) {
            setBookMutation(function (prev) { return (__assign(__assign({}, prev), { coverImg: coverImg })); });
            isUpdated.current = true;
        }
    }, [coverImg]);
    react_1.useEffect(function () {
        // 업데이트가 되었다면 서버에 요청을 보낸다.
        if (isUpdated.current) {
            mutateAsync(bookMutation);
        }
    }, [saveActive]);
    // 업데이트가 완료되면 알림을 띄우고 isUpdated를 초기화한다.
    react_1.useEffect(function () {
        if (!isLoading && isUpdated.current) {
            isUpdated.current = false; // 초기화
            if (isError) {
                flowAlert_1["default"]("error", "서버에 데이터를 저장하지 못했습니다. <br> 잠시후에 다시 시도해주세요.");
                return;
            }
            flowAlert_1["default"]("success", "책 정보가 업데이트 되었습니다.");
        }
    }, [isLoading]);
    return (React.createElement(React.Fragment, null,
        React.createElement(MutationSaveBtn_1["default"], { setSaveActive: setSaveActive, isUpdated: isUpdated }),
        React.createElement("div", { className: "px-24 mt-16" },
            React.createElement("div", { className: "sm:col-span-2" },
                React.createElement("label", { htmlFor: "title", className: "block mb-2 text-md font-medium text-gray-900" }, "\uCC45 \uCCB4\uBAA9"),
                React.createElement("input", { value: bookMutation.title, onChange: handleBookTitle, onKeyDown: handleTitleInputEnterPress, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })),
            React.createElement("br", null),
            React.createElement("br", null),
            React.createElement("div", { className: "block mb-2 text-md font-medium text-gray-900" }, "\uCC45 \uCEE4\uBC84 \uC774\uBBF8\uC9C0"),
            React.createElement(BookCoverImgCropper_1["default"], { defaultSrc: outline.coverImgUrl, cropedFilename: "book-" + bookId + "-coverImg", setFileDate: setCoverImg }))));
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
}
exports["default"] = BookForm;
