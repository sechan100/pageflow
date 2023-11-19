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
var MutationSaveBtn_1 = require("../MutationSaveBtn");
var flowAlert_1 = require("../../../etc/flowAlert");
var react_router_dom_1 = require("react-router-dom");
var chapter_api_1 = require("../../../api/chapter-api");
function ChapterForm() {
    var bookId = react_1.useContext(App_1.QueryContext).bookId;
    var chapterId = react_router_dom_1.useParams().chapterId;
    var outline = outline_api_1.useGetOutlineQuery(bookId);
    var _a = react_1.useState(getChapterSummary(chapterId)), chapter = _a[0], setChapter = _a[1];
    var isUpdated = react_1.useRef(false); // 실제로 데이터가 업데이트 되었는지를 기록, 불필요한 서버 통신을 사전에 막는다.
    var _b = react_1.useState(false), saveActive = _b[0], setSaveActive = _b[1]; // Save 버튼의 클릭 상태를 상위 컴포넌트로 끌어올리기 위한 state
    var _c = react_1.useState({
        title: chapter.title
    }), chapterMutation = _c[0], setChapterMutation = _c[1];
    var _d = chapter_api_1.useChapterMutation(chapter.id), mutateAsync = _d[0], isLoading = _d[1], isError = _d[2];
    react_1.useEffect(function () {
        var newChapter = getChapterSummary(chapterId);
        setChapter(newChapter);
    }, [outline, chapterId]);
    // outline으로인한 chapter 데이터의 변경시, 이미 선언된 state인 chapterMutation의 상태를 업데이트하기 위함
    react_1.useEffect(function () {
        if (chapter) {
            setChapterMutation({
                title: chapter.title
            });
        }
    }, [chapter]);
    react_1.useEffect(function () {
        // 업데이트가 되었다면 서버에 요청을 보낸다.
        if (isUpdated.current) {
            mutateAsync(chapterMutation);
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
            else {
                flowAlert_1["default"]("success", "챕터 정보가 업데이트 되었습니다.");
            }
        }
    }, [isLoading]);
    return (React.createElement(React.Fragment, null,
        React.createElement(MutationSaveBtn_1["default"], { setSaveActive: setSaveActive, isUpdated: isUpdated }),
        React.createElement("div", { className: "px-24 mt-16" },
            React.createElement("div", { className: "sm:col-span-2" },
                React.createElement("label", { htmlFor: "title", className: "block mb-2 text-md font-medium text-gray-900" }, "\uCC55\uD130 \uCCB4\uBAA9"),
                React.createElement("input", { value: chapterMutation.title, onChange: handleChapterTitle, onKeyDown: handleTitleInputEnterPress, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC55\uD130 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })))));
    function handleChapterTitle(e) {
        setChapterMutation(function (prev) { return (__assign(__assign({}, prev), { title: e.target.value })); });
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
    function getChapterSummary(chapterIdStr) {
        var _a;
        var chapterId = parseInt(chapterIdStr);
        var fallbackChapter = {
            id: 0,
            title: "챕터가 로딩중입니다...",
            sortPriority: 10000,
            pages: []
        };
        if (outline.chapters) {
            var chapter_1 = (_a = outline.chapters) === null || _a === void 0 ? void 0 : _a.find(function (chapter) { return chapter.id === chapterId; });
            if (chapter_1) {
                return chapter_1;
                // fallback Chapter인 경우.
            }
            else if (outline.chapters[0].id === 0) {
                return outline.chapters[0];
            }
            else {
                return fallbackChapter;
            }
        }
        else {
            flowAlert_1["default"]("warning", "해당 책에 소속된 챕터가 없습니다.");
            throw new Error("해당 책에 소속된 챕터가 없습니다.");
        }
    }
}
exports["default"] = ChapterForm;
