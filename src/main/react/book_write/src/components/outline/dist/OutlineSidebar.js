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
exports.pageDraggablePrefix = exports.chapterDraggablePrefix = exports.pageDropAreaPrefix = void 0;
var BookBasicPage_1 = require("../outline/BookBasicPage");
var Chapter_1 = require("./Chapter");
var OutlineSidebarWrapper_1 = require("./OutlineSidebarWrapper");
var book_apis_1 = require("../../api/book-apis");
var react_beautiful_dnd_1 = require("react-beautiful-dnd");
exports.pageDropAreaPrefix = 'pageDropArea-';
exports.chapterDraggablePrefix = 'chapter-';
exports.pageDraggablePrefix = 'page-';
function OutlineSidebar(drillingProps) {
    var bookId = drillingProps.bookId, queryClient = drillingProps.queryClient;
    var outline = book_apis_1.useGetOutline(bookId);
    // queryClient의 ["book", bookId]로 저장된 서버 스냅샷을 업데이트한다. 
    function setStateChapters(newChapters) {
        var newOutline = __assign(__assign({}, outline), { chapters: newChapters });
        queryClient.setQueryData(["book", bookId], newOutline);
    }
    // queryClient의 ["book", bookId]로 저장된 서버 스냅샷을 업데이트한다. 
    function setStatePages(pages, sourceChapterId) {
        if (!outline.chapters)
            return;
        var newChapters = outline.chapters.map(function (chapter) {
            // 변경된 페이지를 가지는 챕터는 새로운 페이지로 교체
            if (chapter.id === sourceChapterId) {
                return __assign(__assign({}, chapter), { pages: pages });
                // 나머지 페이지는 유지
            }
            else {
                return chapter;
            }
        });
        setStateChapters(newChapters);
    }
    return (React.createElement(OutlineSidebarWrapper_1["default"], __assign({}, drillingProps),
        React.createElement(BookBasicPage_1["default"], { bookId: outline.id }),
        React.createElement(react_beautiful_dnd_1.Droppable, { droppableId: "chapter-outline", type: 'CHAPTER' }, function (provided) {
            var _a;
            return (React.createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps), (_a = outline.chapters) === null || _a === void 0 ? void 0 :
                _a.map(function (chapter, index) { return (React.createElement(react_beautiful_dnd_1.Draggable, { key: exports.chapterDraggablePrefix + chapter.id, draggableId: exports.chapterDraggablePrefix + chapter.id, index: index }, function (provided) { return (React.createElement("div", __assign({ ref: provided.innerRef }, provided.draggableProps, provided.dragHandleProps),
                    React.createElement(Chapter_1["default"], { chapter: chapter }))); })); }),
                provided.placeholder));
        })));
}
exports["default"] = OutlineSidebar;
