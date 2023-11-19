/* eslint-disable react-hooks/exhaustive-deps */
import { useContext, useEffect, useRef, useState } from "react";
import flowAlert from "../../etc/flowAlert";
import { useBookMutation } from "../../api/book-api";
import { QueryContext } from "../../App";
import { useBookMutationStore } from "./pages/BookForm";
import { useOutlineMutationStore } from "../BookEntityDraggableContext";
import { useOutlineMutation } from "../../api/outline-api";




// const useOutlineMutationStore = create<>((set) => ({

//   isMutateds : {
//     outline: false, // 재배열 변경사항
//     book: false, // 책 정보 변경사항
//     chapter : false // 챕터 정보 변경사항
//   },

//   outlineMutation: {id: null, chapters: null}, // 서버에 요청하기 위한 객체
//   outlineMutateFns: {
//     setChapters : (chapters : string[]) => {
//       set((state : any) => ({
//         outlineMutation: {
//           ...state.outlineMutation,
//           chapters: chapters
//         }
//       }));
//     },
//   },

//   chapterMutation: {title: null}, // 챕터 정보 변경사항을 서버에 요청하기 위한 객체
//   chapterMutateFns: {
//     setTitle : (title : string) => {

//       set((state : any) => ({
//         chapterMutation: {
//           ...state.chapterMutation,
//           title: title
//         }
//       }));
//       set((state : any) => ({
//         isMutateds: {
//           ...state.isMutateds,
//           chapter: true
//         }
//       }));

//     }
//   },

// }));


export default function MutationSaveBtn(){

  const { bookId } = useContext(QueryContext);
  const updateAlertTooltip = useRef<HTMLDivElement>(null);

  const bookStore = useBookMutationStore(); // bookForm의 zusatnd store
  const outlineStore = useOutlineMutationStore(); // outlineSidebar의 zustand store
  const [isAnyMutation, setIsAnyMutation] = useState(false); // outline, book, chapter중 하나라도 변경사항이 있다면 true.

  // outline, book, chapter 중 하나라도 변경 사항이 있다면 hasAnyMutation을 true로 변경한다.
  useEffect(() => {
    if(isAnyMutated([outlineStore.isMutated, bookStore.isMutated])){
      setIsAnyMutation(true);
    }
  }, [outlineStore.isMutated, bookStore.isMutated]);


  // 하나라도 변경사항이 존재할 경우 호출
  useEffect(() => {
    updateAlertPingHandler(); // 사용자에게 변경사항이 있음을 알리는 핑 ON
  }, [isAnyMutation]);


// ================================================================================

  const bookMutateQuery = useBookMutation(bookId); // book에 관한 변경사항을 서버에 요청하기 위한 react-query custom hook
  const outlineMutateQuery = useOutlineMutation(bookId); // outline에 관한 변경사항을 서버에 요청하기 위한 react-query custom hook



  // 변경사항을 서버에 요청 -> Promise.all을 통해서 여러개의 비동기 요청을 하나의 트랜잭션으로 처리한다.
  function flushMutations(){
    if(isAnyMutation){

      // 변경된 데이터들
      const outlineUpdatePromise = outlineStore.isMutated ? outlineMutateQuery.mutateAsync(outlineStore.payload) : null;
      const bookUpdatePromise = bookStore.isMutated ? bookMutateQuery.mutateAsync(bookStore.payload) : null;

      // 서버에 요청이 필요한 데이터의 mutateAsync에서 반환하는 Promise 배열
      let updateApiPromises : any[] = [
        outlineUpdatePromise, 
        bookUpdatePromise
      ];

      // promise가 null이라면 변경사항이 존재하지 않는 데이터이므로, 배열에서 제거한다.
      updateApiPromises = updateApiPromises.filter((promise) => promise !== null);

      // null인 promise가 제거된 Promise 배열로 Promise.all을 호출한다.
      Promise.all(updateApiPromises)

      .then((response) => {
        console.log("서버 응답", response);
        flowAlert("success", "저장되었습니다.");
      })

      .catch((error) => {
        console.log(error);
        flowAlert("error", "서버에 데이터를 저장하지 못했습니다. <br> 잠시후에 다시 시도해주세요.");
      })
      
    }
  }


  return (
    <div onClick={flushMutations} className="flex justify-start fixed z-50 right-7 top-7">
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
  );







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