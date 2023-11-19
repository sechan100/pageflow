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
exports.inClosingPageDropAreaPrefix = void 0;
var react_1 = require("react");
var Page_1 = require("./Page");
var react_2 = require("react");
var react_beautiful_dnd_1 = require("react-beautiful-dnd");
var OutlineContext_1 = require("../OutlineContext");
var react_router_dom_1 = require("react-router-dom");
exports.inClosingPageDropAreaPrefix = 'inClosingPageDropArea-';
function Chapter(props) {
    var _a = react_2.useState(true), isPagesHidden = _a[0], setIsPagesHidden = _a[1];
    var innerPageList = react_1.useRef(null);
    var chapterFormLink = react_1.useRef(null);
    var chapter = props.chapter;
    // 챕터의 open 상태를 기록하는 ref
    var _b = props.chapterOpenStatus, openedChapterIds = _b.openedChapterIds, addOpenedChapterIds = _b.addOpenedChapterIds, removeOpenedChapterIds = _b.removeOpenedChapterIds;
    react_1.useEffect(function () {
        // 처음 렌더링될 때, openedChapterIds.current에 챕터가 있으면 챕터를 open한다. 
        if (openedChapterIds.current.includes("chapter-id-" + chapter.id) && isPagesHidden) {
            // @ts-ignore
            innerPageList.current.classList.remove('hidden');
            // @ts-ignore
            setIsPagesHidden(false);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);
    function toggleInnerPageList() {
        if (innerPageList.current) {
            // @ts-ignore
            innerPageList.current.classList.toggle('hidden');
            setIsPagesHidden(!isPagesHidden);
            // openedChapterIds.current에 챕터가 없으면 추가, 있으면 제거
            if (isPagesHidden) {
                // @ts-ignore
                addOpenedChapterIds(innerPageList.current.id);
            }
            else {
                // @ts-ignore
                removeOpenedChapterIds(innerPageList.current.id);
            }
        }
    }
    return (react_1["default"].createElement("div", { className: "relative" },
        react_1["default"].createElement(react_beautiful_dnd_1.Droppable, { droppableId: exports.inClosingPageDropAreaPrefix + String(chapter.id), isDropDisabled: !isPagesHidden, type: 'PAGE' }, function (provided, snapshot) { return (react_1["default"].createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps),
            react_1["default"].createElement("div", { onMouseOver: toggleChapterFormLink, onMouseOut: toggleChapterFormLink, onClick: toggleInnerPageList, className: (snapshot.isDraggingOver ? "bg-gray-700 " : "") + "flex items-center p-1 w-full text-base font-normal text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-100 dark:text-white dark:hover:bg-gray-700 bg-gray-800" },
                react_1["default"].createElement("svg", { "aria-hidden": "true", className: "w-4 h-4 text-gray-800 dark:text-white", fill: "none", viewBox: "0 0 16 12" },
                    react_1["default"].createElement("path", { stroke: "currentColor", strokeLinecap: "round", strokeLinejoin: "round", strokeWidth: "1.5", d: "M1 1h14M1 6h14M1 11h7" })),
                react_1["default"].createElement("span", { className: "flex-1 ml-3 text-left whitespace-nowrap" }, chapter.title),
                react_1["default"].createElement("svg", { className: "w-4 h-4", fill: "currentColor", viewBox: "0 0 20 20", xmlns: "http://www.w3.org/2000/svg" },
                    react_1["default"].createElement("path", { fillRule: "evenodd", d: "M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z", clipRule: "evenodd" }))))); }),
        react_1["default"].createElement(react_router_dom_1.Link, { to: "/chapter/" + props.chapter.id, onMouseOver: toggleChapterFormLink, onMouseOut: toggleChapterFormLink, className: "hidden absolute inline z-50 px-1.5 py-1 -ml-1 -mt-1 -left-1/5 top-1.5 bg-gray-700 items-center text-white text-sm font-normal text-gray-900 rounded-lg transition duration-75 group", ref: chapterFormLink },
            react_1["default"].createElement("svg", { className: "w-5 h-5 text-gray-800 dark:text-white", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 20 20" },
                react_1["default"].createElement("path", { stroke: "currentColor", strokeLinecap: "round", strokeLinejoin: "round", strokeWidth: "1.5", d: "M15 17v1a.97.97 0 0 1-.933 1H1.933A.97.97 0 0 1 1 18V5.828a2 2 0 0 1 .586-1.414l2.828-2.828A2 2 0 0 1 5.828 1h8.239A.97.97 0 0 1 15 2M6 1v4a1 1 0 0 1-1 1H1m13.14.772 2.745 2.746M18.1 5.612a2.086 2.086 0 0 1 0 2.953l-6.65 6.646-3.693.739.739-3.692 6.646-6.646a2.087 2.087 0 0 1 2.958 0Z" }))),
        react_1["default"].createElement("ul", { className: "hidden py-2 space-y-2", ref: innerPageList, id: "chapter-id-" + chapter.id },
            react_1["default"].createElement(react_beautiful_dnd_1.Droppable, { droppableId: OutlineContext_1.pageDropAreaPrefix + chapter.id, type: 'PAGE' }, function (provided) {
                var _a;
                return (react_1["default"].createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps), (_a = chapter.pages) === null || _a === void 0 ? void 0 :
                    _a.map(function (page, index) { return (react_1["default"].createElement(react_beautiful_dnd_1.Draggable, { key: OutlineContext_1.pageDraggablePrefix + page.id, draggableId: OutlineContext_1.pageDraggablePrefix + page.id, index: index }, function (provided) { return (react_1["default"].createElement("div", __assign({ ref: provided.innerRef }, provided.draggableProps, provided.dragHandleProps),
                        react_1["default"].createElement(Page_1["default"], { page: page }))); })); }),
                    provided.placeholder));
            }))));
    // Chapter 요소에 hover시에, chapter 옆에 ChapterForm으로 들어가는 링크 버튼을 보여주는 함수
    function toggleChapterFormLink() {
        if (chapterFormLink.current) {
            // @ts-ignore
            chapterFormLink.current.classList.toggle('hidden');
        }
    }
}
exports["default"] = Chapter;
