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
exports.useBookMutationStore = void 0;
var react_1 = require("react");
var App_1 = require("../../../App");
var outline_api_1 = require("../../../api/outline-api");
var BookCoverImgCropper_1 = require("../BookCoverImgCropper");
var zustand_1 = require("zustand");
exports.useBookMutationStore = zustand_1.create(function (set) { return ({
    payload: { title: null, coverImg: null },
    isMutated: false,
    resetMutation: function () { return set(function (state) { return (__assign(__assign({}, state), { payload: { title: null, coverImg: null }, isMutated: false })); }); },
    isLoading: false,
    dispatchs: {
        setTitle: function (title) {
            set(function (state) { return ({
                payload: __assign(__assign({}, state.payload), { title: title }),
                isMutated: true
            }); });
        },
        setCoverImg: function (coverImg) {
            set(function (state) { return ({
                payload: __assign(__assign({}, state.payload), { coverImg: coverImg }),
                isMutated: true
            }); });
        }
    }
}); });
function BookForm() {
    var bookId = react_1.useContext(App_1.QueryContext).bookId;
    var outline = outline_api_1.useGetOutlineQuery(bookId);
    var bookStore = exports.useBookMutationStore();
    var _a = react_1.useReducer(localBookReducer, bookStore.payload), localBook = _a[0], localBookDispatch = _a[1]; // zustand store에 변경사항을 업데이트하기 전에 임시로 저장하는 로컬 상태
    // outline 데이터의 변경시, 이미 선언된 state인 bookMutation의 상태를 업데이트하기 위함
    react_1.useEffect(function () {
        if (outline) {
            localBookDispatch({ type: 'TITLE', payload: outline.title });
        }
    }, [outline]);
    // 로컬 데이터의 변경사항을 zustand store에 업데이트
    react_1.useEffect(function () {
        // local의 title 데이터가 존재하면서 outline의 title 데이터와 다를 경우 => title 변경사항이 존재
        if (localBook.title && isTitleChanged(localBook.title))
            bookStore.dispatchs.setTitle(localBook.title);
        // local의 coverImg 데이터가 null이 아니라면 => coverImg 변경사항이 존재
        if (localBook.coverImg)
            bookStore.dispatchs.setCoverImg(localBook.coverImg);
    }, [localBook]);
    return (React.createElement(React.Fragment, null,
        React.createElement("div", { className: "px-24 mt-16" },
            React.createElement("div", { className: "sm:col-span-2" },
                React.createElement("label", { htmlFor: "title", className: "block mb-2 text-md font-medium text-gray-900" }, "\uCC45 \uCCB4\uBAA9"),
                React.createElement("input", { value: localBook.title !== null ? localBook.title : '', onChange: function (e) { return localBookDispatch({ type: "TITLE", payload: e.target.value }); }, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })),
            React.createElement("br", null),
            React.createElement("br", null),
            React.createElement("div", { className: "block mb-2 text-md font-medium text-gray-900" }, "\uCC45 \uCEE4\uBC84 \uC774\uBBF8\uC9C0"),
            React.createElement(BookCoverImgCropper_1["default"], { defaultSrc: outline.coverImgUrl, cropedFilename: "book-" + bookId + "-coverImg", setFileDate: localBookDispatch }))));
    function localBookReducer(state, action) {
        switch (action.type) {
            case 'TITLE':
                return __assign(__assign({}, state), { title: action.payload });
            case 'COVER_IMG':
                return __assign(__assign({}, state), { coverImg: action.payload });
            default:
                return state;
        }
    }
    // 현재 서버 상태에 저장된 데이터와 다른지...
    function isTitleChanged(title) {
        return outline.title !== title;
    }
}
exports["default"] = BookForm;
