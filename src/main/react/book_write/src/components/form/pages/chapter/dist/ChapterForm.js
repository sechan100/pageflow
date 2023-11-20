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
var __spreadArrays = (this && this.__spreadArrays) || function () {
    for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
    for (var r = Array(s), k = 0, i = 0; i < il; i++)
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
            r[k] = a[j];
    return r;
};
exports.__esModule = true;
exports.useChapterMutationStore = void 0;
var react_1 = require("react");
var App_1 = require("../../../../App");
var outline_api_1 = require("../../../../api/outline-api");
var zustand_1 = require("zustand");
var react_router_dom_1 = require("react-router-dom");
exports.useChapterMutationStore = zustand_1.create(function (set) { return ({
    payload: [],
    isMutated: false,
    resetMutation: function () { return set(function (state) { return (__assign(__assign({}, state), { payload: [], isMutated: false })); }); },
    isLoading: false,
    dispatchs: {
        updateChapter: function (_a) {
            var id = _a.id, title = _a.title;
            set(function (state) {
                // 해당 chapterId를 가진 변경사항이 존재하는 경우, 해당 부분을 수정, 
                // 존재하지 않는 경우, 새로운 chapterMutation을 추가한다.
                var isExist = state.payload.find(function (chapter) { return chapter.id === id; });
                // 기존 변경사항이 존재하는 경우
                if (isExist) {
                    return __assign(__assign({}, state), { payload: state.payload.map(function (chapter) {
                            if (chapter.id === id)
                                return __assign(__assign({}, chapter), { title: title });
                            else
                                return chapter;
                        }), isMutated: true });
                    // 기존 변경사항이 존재하지 않는 경우  
                }
                else {
                    return __assign(__assign({}, state), { payload: __spreadArrays(state.payload, [
                            {
                                id: id,
                                title: title
                            }
                        ]), isMutated: true });
                }
            });
        }
    }
}); });
function ChapterForm() {
    var chapterId = react_router_dom_1.useParams().chapterId;
    var bookId = react_1.useContext(App_1.QueryContext).bookId;
    var outline = outline_api_1.useGetOutlineQuery(bookId);
    var chapter = getChapterById(Number(chapterId));
    var chapterStore = exports.useChapterMutationStore();
    var _a = react_1.useReducer(localChapterReducer, chapterStore.payload), localChapter = _a[0], localChapterDispatch = _a[1]; // zustand store에 변경사항을 업데이트하기 전에 임시로 저장하는 로컬 상태
    // outline 데이터의 변경시, 이미 선언된 state인 ChapterMutation의 상태를 업데이트하기 위함
    react_1.useEffect(function () {
        if (outline) {
            localChapterDispatch({ type: 'TITLE', payload: chapter ? chapter.title : null });
        }
    }, [outline, chapterId]);
    // 로컬 데이터의 변경사항을 zustand store에 업데이트
    react_1.useEffect(function () {
        // local의 title 데이터가 존재하면서 outline의 title 데이터와 다를 경우 => title 변경사항이 존재
        if (localChapter.title && isTitleChanged(localChapter.title))
            chapterStore.dispatchs.updateChapter({ id: Number(chapterId), title: localChapter.title });
    }, [localChapter]);
    return (React.createElement(React.Fragment, null,
        React.createElement("div", { className: "px-24 mt-16" },
            React.createElement("div", { className: "sm:col-span-2" },
                React.createElement("label", { htmlFor: "title", className: "block mb-2 text-md font-medium text-gray-900" }, "\uCC55\uD130 \uC81C\uBAA9"),
                React.createElement("input", { value: localChapter.title ? localChapter.title : '', onChange: handleTitleChange, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })))));
    function localChapterReducer(state, action) {
        switch (action.type) {
            case 'TITLE':
                return __assign(__assign({}, state), { title: action.payload });
            default:
                return state;
        }
    }
    // 현재 서버 상태에 저장된 데이터와 다른지...
    function isTitleChanged(title) {
        return (chapter === null || chapter === void 0 ? void 0 : chapter.title) !== title;
    }
    function getChapterById(chapterId) {
        var _a;
        return (_a = outline.chapters) === null || _a === void 0 ? void 0 : _a.find(function (chapter) { return chapter.id === chapterId; });
    }
    function handleTitleChange(e) {
        localChapterDispatch({ type: "TITLE", payload: e.target.value });
    }
}
exports["default"] = ChapterForm;
