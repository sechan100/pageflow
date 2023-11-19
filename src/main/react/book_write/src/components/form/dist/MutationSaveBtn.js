"use strict";
exports.__esModule = true;
/* eslint-disable react-hooks/exhaustive-deps */
var react_1 = require("react");
var flowAlert_1 = require("../../etc/flowAlert");
var book_api_1 = require("../../api/book-api");
var App_1 = require("../../App");
var BookForm_1 = require("./pages/BookForm");
var BookEntityDraggableContext_1 = require("../BookEntityDraggableContext");
var outline_api_1 = require("../../api/outline-api");
var chapter_api_1 = require("../../api/chapter-api");
var NewChapterBtn_1 = require("../outline/newItemBtn/NewChapterBtn");
var ChapterForm_1 = require("./pages/ChapterForm");
function MutationSaveBtn() {
    var bookId = react_1.useContext(App_1.QueryContext).bookId;
    var updateAlertTooltip = react_1.useRef(null);
    // 서버 요청 메소드를 저장하는 ref 변수.
    // 이벤트 리스너를 등록시에, 등록 당시에, 클로저를 형성하여 당시 상태의 변수들의 스냅샷에 접근하기 때문에, 최신 상태의 함수를 참조하도록 할 필요가 있다.
    var flushMutationRef = react_1.useRef(flushMutations);
    react_1.useEffect(function () {
        flushMutationRef.current = flushMutations;
    }, [flushMutations]);
    // ##### zustand stores ######
    var bookStore = BookForm_1.useBookMutationStore(); // bookForm의 zusatnd store
    var outlineStore = BookEntityDraggableContext_1.useOutlineMutationStore(); // outlineSidebar의 zustand store
    var createChapterStore = NewChapterBtn_1.useCreateChapterStore(); // newChapterBtn의 zustand store
    var chapterStore = ChapterForm_1.useChapterMutationStore(); // chapterForm의 zustand store
    // ############################
    var mutationStores = [bookStore, outlineStore, createChapterStore, chapterStore]; // zustand store들을 배열로 저장
    var isMutateds = mutationStores.map(function (store) { return store.isMutated; }); // store들의 isMutated를 배열로 저장
    var _a = react_1.useState(false), isAnyMutation = _a[0], setIsAnyMutation = _a[1]; // outline, book, chapter중 하나라도 변경사항이 있다면 true.
    // outline, book, chapter 중 하나라도 변경 사항이 있다면 hasAnyMutation을 true로 변경한다.
    react_1.useEffect(function () {
        if (isAnyMutated(isMutateds)) {
            setIsAnyMutation(true);
        }
        else {
            setIsAnyMutation(false);
        }
    }, isMutateds);
    // 하나라도 변경사항이 존재할 경우, 사용자에게 변경사항이 있음을 알리는 핑을 띄움
    react_1.useEffect(function () {
        updateAlertPingHandler();
    }, [isAnyMutation]);
    // 새로운 챕터 생성 요청은 바로 서버에 요청한다.
    react_1.useEffect(function () {
        if (createChapterStore.isMutated)
            flushMutations();
    }, [createChapterStore.isMutated]);
    // 저장 단축키 Ctrl + S 등록
    react_1.useEffect(function () {
        var handleKeyPress = function (event) {
            if ((event.ctrlKey || event.metaKey) && event.keyCode === 83) {
                // 'Ctrl + s'가 눌렸을 때 실행할 동작
                event.preventDefault();
                flushMutationRef.current();
            }
        };
        // 이벤트 리스너 추가
        document.addEventListener('keydown', handleKeyPress);
        // 컴포넌트가 언마운트될 때 이벤트 리스너 제거
        return function () {
            document.removeEventListener('keydown', handleKeyPress);
        };
    }, []);
    // ================================================================================
    var bookMutateQuery = book_api_1.useBookMutation(bookId); // book에 관한 변경사항을 서버에 요청하기 위한 react-query custom hook
    var outlineMutateQuery = outline_api_1.useOutlineMutation(bookId); // outline에 관한 변경사항을 서버에 요청하기 위한 react-query custom hook
    var createChapterQuery = chapter_api_1.useCreateChapterMutation(bookId);
    var chapterMutateQuery = chapter_api_1.useChapterMutation();
    // 변경사항을 서버에 요청 -> Promise.all을 통해서 여러개의 비동기 요청을 하나의 트랜잭션으로 처리한다.
    function flushMutations() {
        // 변경된 데이터들
        var updateOutlinePromise = outlineStore.isMutated ? outlineMutateQuery.mutateAsync(outlineStore.payload) : null;
        var updateBookPromise = bookStore.isMutated ? bookMutateQuery.mutateAsync(bookStore.payload) : null;
        var createChapterPromise = createChapterStore.isMutated ? createChapterQuery.mutateAsync() : null;
        var updateChapterPromise = chapterStore.isMutated ? chapterMutateQuery.mutateAsync(chapterStore.payload) : null;
        // 서버에 요청이 필요한 데이터의 mutateAsync에서 반환하는 Promise 배열
        var updateApiPromises = [
            updateOutlinePromise,
            updateBookPromise,
            createChapterPromise,
            updateChapterPromise
        ];
        // promise가 null이라면 변경사항이 존재하지 않는 데이터이므로, 배열에서 제거한다.
        updateApiPromises = updateApiPromises.filter(function (promise) { return promise !== null; });
        // null인 promise가 제거된 Promise 배열로 Promise.all을 호출한다.
        Promise.all(updateApiPromises)
            .then(function (response) {
            console.log("서버 응답", response);
            resetStoreMutations();
            flowAlert_1["default"]("success", "변경사항을 저장했습니다.");
        })["catch"](function (error) {
            console.log(error);
            flowAlert_1["default"]("error", "서버에 데이터를 저장하지 못했습니다. <br> 잠시후에 다시 시도해주세요.");
        });
    }
    return (React.createElement("div", { onClick: flushMutationsOnClick, className: "flex justify-start fixed z-50 right-7 top-7" },
        isAnyMutation &&
            React.createElement("div", { className: "relative flex items-center mb-2 mr-3 transition-opacity duration-[1500ms] opacity-0", ref: updateAlertTooltip },
                React.createElement("div", { className: "tooltip bg-white text-black border border-gray-300 py-1 px-2 rounded shadow-lg" },
                    "\uBCC0\uACBD\uC0AC\uD56D\uC774 \uC788\uC2B5\uB2C8\uB2E4",
                    React.createElement("div", { className: "tooltip-arrow absolute top-[40%] right-1 w-0 h-0 border-transparent border-solid border-l-2 border-t-2 border-b-2 transform -translate-y-1/2 -translate-x-1/2" }))),
        React.createElement("div", { className: (isAnyMutation ? "bg-gray-700 hover:bg-gray-900" : "bg-gray-500") + " w-12 h-12 p-3 mb-3 rounded-full cursor-pointer" },
            isAnyMutation &&
                React.createElement("span", { className: "absolute top-1 right-[1px] flex h-3 w-3" },
                    React.createElement("span", { className: "animate-ping absolute inline-flex h-full w-full rounded-full bg-sky-400 opacity-75" }),
                    React.createElement("span", { className: "relative inline-flex rounded-full h-3 w-3 bg-sky-500" })),
            React.createElement("svg", { className: "w-6 h-6 text-gray-800 dark:text-white", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 16 18" },
                React.createElement("path", { stroke: "currentColor", strokeLinecap: "round", strokeLinejoin: "round", strokeWidth: "1.5", d: "M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3" })))));
    // 클릭 이벤트 핸들러
    function flushMutationsOnClick() {
        if (isAnyMutation) {
            flushMutations();
        }
    }
    // 모든 변경 사항들을 초기화
    function resetStoreMutations() {
        mutationStores.forEach(function (store) {
            store.resetMutation();
        });
    }
    // 업데이트가 있음을 알리는 핑 띄우기
    function updateAlertPingHandler() {
        setTimeout(function () {
            if (updateAlertTooltip.current) {
                updateAlertTooltip.current.classList.remove("opacity-0");
            }
        }, 100);
    }
    // 업데이트 된 값이 하나라도 있으면 true.
    function isAnyMutated(isMutateds) {
        for (var i = 0; i < isMutateds.length; i++) {
            if (isMutateds[i])
                return true;
        }
        return false;
    }
}
exports["default"] = MutationSaveBtn;
