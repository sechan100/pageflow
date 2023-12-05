/* eslint-disable react-hooks/exhaustive-deps */
import { useContext, useEffect, useRef, useState } from "react";
import { useBookMutation } from "../../api/book-api";
import { QueryContext } from "../../App";
import  flowAlert  from "../../etc/flowAlert";
import { useBookMutationStore } from "../form/pages/book/BookForm";
import { useOutlineMutationStore } from "../BookEntityDraggableContext";
import { useOutlineMutation } from "../../api/outline-api";
import { useChapterMutation, useCreateChapterMutation } from "../../api/chapter-api";
import { useCreateChapterStore } from "../outline/newItemBtn/NewChapterBtn";
import { useChapterMutationStore } from "../form/pages/chapter/ChapterForm";
import { useCreatePageMutation, usePageMutation } from "../../api/page-api";
import { useCreatePageStore } from "../outline/newItemBtn/NewPageBtn";
import { usePageMutationStore } from "../form/pages/page/PageForm";
import _ from "lodash";
import { create } from "zustand";




interface UseAutoSaveStroe {isAutoSaveAvailable: {current: boolean};}
// 자동 저장을 특정시점동안만 비활성화 시키기 위한 zustand store
// 예를 들어, 챕터 드래깅 중에는 자동저장을 비활성화 시키고, 챕터 드래깅이 끝나면 다시 활성화 시킨다.
// 이를 위해서 렌더링에 관여하는 상태가 아닌, current를 직접 변경하는 방식을 사용하여, 렌더링 사이클에 포함시키지 않는다.
export const useAutoSaveStore = create<UseAutoSaveStroe>((set) => ({
  isAutoSaveAvailable: {current: true}
}));



export default function MutationSaveBtn(){

  const { bookId } = useContext(QueryContext);
  const updateAlertTooltip = useRef<HTMLDivElement>(null);
  const { isAutoSaveAvailable } = useAutoSaveStore();

  // ##### mutation zustand stores ######
  const bookStore = useBookMutationStore(); // bookForm의 zusatnd store
  const outlineStore = useOutlineMutationStore(); // outlineSidebar의 zustand store
  const createChapterStore = useCreateChapterStore(); // newChapterBtn의 zustand store
  const createPageStore = useCreatePageStore(); // newPageBtn의 zustand store
  const chapterStore = useChapterMutationStore(); // chapterForm의 zustand store
  const pageStore = usePageMutationStore(); // pageForm의 zustand store
  // ############################

  const mutationStores = [bookStore, outlineStore, createChapterStore, chapterStore, createPageStore, pageStore]; // zustand store들을 배열로 저장
  
  const isMutateds : boolean[] = mutationStores.map((store) => store.isMutated); // store들의 isMutated를 배열로 저장
  const [isAnyMutation, setIsAnyMutation] = useState(false); // outline, book, chapter중 하나라도 변경사항이 있다면 true.

  // outline, book, chapter 중 하나라도 변경 사항이 있다면 hasAnyMutation을 true로 변경한다.
  useEffect(() => {
    if(isAnyMutated(isMutateds)){
      setIsAnyMutation(true);
    } else {
      setIsAnyMutation(false);
    }
  }, isMutateds);


  // 하나라도 변경사항이 존재할 경우, 사용자에게 변경사항이 있음을 알리는 핑을 띄운다.
  useEffect(() => {
    updateAlertPingHandler();
  }, [isAnyMutation]);


  // 챕터 생성 요청과 페이지 생성 요청은 바로 서버에 요청한다.
  useEffect(() => {
    if(createChapterStore.isMutated) flushMutations(false);
    if(createPageStore.isMutated) flushMutations(false);
  }, [createChapterStore.isMutated, createPageStore.isMutated]);


  // 저장 단축키 Ctrl + S 등록
  useEffect(() => {
    const handleKeyPress = (event : any) => {
      if ((event.ctrlKey || event.metaKey) && event.keyCode === 83) {
        // 'Ctrl + s'가 눌렸을 때 실행할 동작
        event.preventDefault();
        if(isAnyMutation){
          flushMutations();
        }
      }
    };
    // 이벤트 리스너 추가
    document.addEventListener('keydown', handleKeyPress);
    
    // 컴포넌트가 언마운트될 때 이벤트 리스너 제거
    return () => {
      document.removeEventListener('keydown', handleKeyPress);
    };
  }, [flushMutations, isAnyMutation, isAutoSaveAvailable.current]);


  // 자동 저장 디바운드 등록
  useEffect(() => {
    const debouncedSave = _.debounce(() => {
      // 변경 사항이 존재하고, isAutoSaveAvailable이 true일 경우에만 자동 저장을 실행한다.
      // isAutoSaveAvailable.current의 값은 다양한 곳에서 실시간으로 변동된다.
      if(isAnyMutation && isAutoSaveAvailable.current){
        flushMutations();
      }
    }, 5000);
    debouncedSave();
    return () => debouncedSave.cancel();
  }, [flushMutations, isAutoSaveAvailable.current]);


// ================================================================================

  const bookMutateQuery = useBookMutation(bookId); // book에 관한 변경사항을 서버에 요청하기 위한 react-query custom hook
  const outlineMutateQuery = useOutlineMutation(bookId); // outline에 관한 변경사항을 서버에 요청하기 위한 react-query custom hook
  const createChapterQuery = useCreateChapterMutation(bookId); 
  const createPageQuery = useCreatePageMutation(bookId, createPageStore.chapterId); 
  const chapterMutateQuery = useChapterMutation(); 
  const pageMutateQuery = usePageMutation();



  // 변경사항을 서버에 요청 -> Promise.all을 통해서 여러개의 비동기 요청을 하나의 트랜잭션으로 처리한다.
  function flushMutations(isTriggeredByUser : boolean = true){
    // 변경된 데이터들
    const updateOutlinePromise = outlineStore.isMutated ? outlineMutateQuery.mutateAsync(outlineStore.payload) : null;
    const updateBookPromise = bookStore.isMutated ? bookMutateQuery.mutateAsync(bookStore.payload) : null;
    const createChapterPromise = createChapterStore.isMutated ? createChapterQuery.mutateAsync() : null;
    const createPagePromise = createPageStore.isMutated ? createPageQuery.mutateAsync() : null;
    const updateChapterPromise = chapterStore.isMutated ? chapterMutateQuery.mutateAsync(chapterStore.payload) : null;
    const updatePagePromise = pageStore.isMutated ? pageMutateQuery.mutateAsync(pageStore.payload) : null;

    // 서버에 요청이 필요한 데이터의 mutateAsync에서 반환하는 Promise 배열
    let updateApiPromises : any[] = [
      updateOutlinePromise, 
      updateBookPromise, 
      createChapterPromise,
      createPagePromise,
      updateChapterPromise,
      updatePagePromise
    ];

    // promise가 null이라면 변경사항이 존재하지 않는 데이터이므로, 배열에서 제거한다.
    updateApiPromises = updateApiPromises.filter((promise) => promise !== null);

    // null인 promise가 제거된 Promise 배열로 Promise.all을 호출한다.
    Promise.all(updateApiPromises)

    .then((response) => {
      console.log("서버 응답", response);
      resetStoreMutations();
      if(isTriggeredByUser) flowAlert("success", "변경사항을 저장했습니다.");
    })

    .catch((error) => {
      if(error.response.status === 403){
        flowAlert("error", "해당 작업에 대한 접근권한이 없습니다.");
        return;
      }
      flowAlert("error", "서버에 데이터를 저장하지 못했습니다. <br> 잠시후에 다시 시도해주세요.");
    })
  }


  return (
  <div className="fixed z-50 right-[5vw] top-7 flex">
    {/* eslint-disable-next-line no-restricted-globals */}
    <div onClick={() => { history.back()}} className="flex justify-start mr-3">
      <div className="bg-gray-700 hover:bg-gray-900 w-12 h-12 p-3 mb-3 rounded-full cursor-pointer">
        <svg className="w-6 h-6 text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
          <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5H1m0 0 4 4M1 5l4-4"/>
        </svg>
      </div>
    </div>

    <div onClick={flushMutationsOnClick} className="flex justify-start">
      {isAnyMutation && 
      <div className="relative flex items-center mb-2 mr-3 transition-opacity duration-[1500ms] opacity-0" ref={updateAlertTooltip}>
        <div className="tooltip bg-white text-black border border-gray-300 py-1 px-2 rounded shadow-lg">
          변경사항이 있습니다
          <div className="tooltip-arrow absolute top-[40%] right-1 w-0 h-0 border-transparent border-solid border-l-2 border-t-2 border-b-2 transform -translate-y-1/2 -translate-x-1/2"></div>
        </div>
      </div>}

      <div className={ (isAnyMutation ? "bg-gray-700 hover:bg-gray-900" : "bg-gray-500") + " w-12 h-12 p-3 mb-3 rounded-full cursor-pointer"}>
      {isAnyMutation && 
        <span className="absolute top-1 right-[1px] flex h-3 w-3">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-sky-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-3 w-3 bg-sky-500"></span>
        </span>}
        <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 16 18">
          <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3"/>
        </svg>
      </div>
    </div>
  </div>
  );



  // 클릭 이벤트 핸들러
  function flushMutationsOnClick(){
    if(isAnyMutation){
      flushMutations();
    }
  }

  // 모든 변경 사항들을 초기화
  function resetStoreMutations(){
    mutationStores.forEach((store) => {
      store.resetMutation();
    });
  }

  // 업데이트가 있음을 알리는 핑 띄우기
  function updateAlertPingHandler(){
    setTimeout(() => {
      if (updateAlertTooltip.current) {
        updateAlertTooltip.current.classList.remove("opacity-0");
      }
    }, 100);
  }

  // 업데이트 된 값이 하나라도 있으면 true.
  function isAnyMutated(isMutateds : boolean[]) : boolean {
    for(let i = 0; i < isMutateds.length; i++){
      if(isMutateds[i]) return true;
    }
    return false;
  }
}