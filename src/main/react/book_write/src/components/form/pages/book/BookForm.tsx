/* eslint-disable react-hooks/exhaustive-deps */

import { useContext, useEffect, useReducer, useState } from "react";
import { QueryContext } from "../../../../App";
import { useGetOutlineQuery } from "../../../../api/outline-api";
import { BookMutation, Outline } from "../../../../types/types";
import ImageCropComponent from "./BookCoverImgCropper";
import { create } from "zustand";
import axios from "axios";
import flowAlert from "../../../../etc/flowAlert";

interface BookMutationStore {
  payload: BookMutation;
  isMutated: boolean;
  resetMutation: () => void;
  isLoading: boolean;
  dispatchs: {
    setTitle : (title : string) => void,
    setCoverImg : (coverImg : File) => void
  }
  resetPayload: () => void;
}

export const useBookMutationStore = create<BookMutationStore>((set : any) => ({
  payload: { title: null, coverImg: null }, // 책 정보 변경사항을 서버에 요청하기 위한 객체
  isMutated: false, // 변경사항이 존재하는지 여부
  resetMutation: () => set(
    (state : any) => ({
      ...state,
      payload: { title: null, coverImg: null },
      isMutated: false
    })
  ),
  isLoading: false, // 서버에 전달한 요청이 진행중인지의 여부. 기본적으로 false이지만, [true로 바뀌는 순간부터, 다시 false가 되는 순간]까지 서버에 요청중임을 의미한다.
  dispatchs: { // bookMutation 상태를 변경하는 dispatch 함수들. 내부적으로 isMutated를 true로 변경하는 로직을 포함한다.

    setTitle : (title : string) => {
      set((state : any) => ({
        payload: {
          ...state.payload,
          title: title
        },
        isMutated: true
      }));
    },

    setCoverImg : (coverImg : File) => {
      set((state : any) => ({
        payload: {
          ...state.payload,
          coverImg: coverImg
        },
        isMutated: true
      }));
    }

  },
  resetPayload: () => set(
    (state : any) => ({
      ...state,
      payload: { title: null, coverImg: null }
    })
  )
}));



export default function BookForm(){

  const { bookId } = useContext(QueryContext);
  const outline : Outline = useGetOutlineQuery(bookId);
  const bookStore = useBookMutationStore();
  const [localBook, localBookDispatch] : [BookMutation, any] = useReducer(localBookReducer, bookStore.payload); // zustand store에 변경사항을 업데이트하기 전에 임시로 저장하는 로컬 상태
  const [editMode, setEditMode] = useState<string>("base"); // base, content


  // outline 데이터의 변경시, 이미 선언된 state인 bookMutation의 상태를 업데이트하기 위함
  useEffect(() => {
    if(outline){
      localBookDispatch({type: 'TITLE', payload: outline.title});
    }
  }, [outline]);

  
  // 로컬 데이터의 변경사항을 zustand store에 업데이트
  useEffect(() => {

    // local의 title 데이터가 존재하면서 outline의 title 데이터와 다를 경우 => title 변경사항이 존재
    if(localBook.title && localBook.title !== outline.title && outline.title !== "책이 로딩중입니다..."){
      console.log(`[제목] ${outline.title} -> ${localBook.title}`)
      bookStore.dispatchs.setTitle(localBook.title);
    }
    
    // local의 coverImg 데이터가 null이 아니라면 => coverImg 변경사항이 존재
    if(localBook.coverImg){
      bookStore.dispatchs.setCoverImg(localBook.coverImg);
      localBook.coverImg = null;
    }
  }, [localBook]);


  return (
  <>
      <div>
        <div className="grid max-w-xs grid-cols-2 gap-1 p-1 mx-auto my-2 bg-gray-200 rounded-lg" role="group">
            <button type="button" onClick={() => setEditMode("base")} className={ (editMode === "base" ? "text-white bg-gray-900" : "text-gray-900 hover:bg-gray-300") + " px-5 py-1.5 text-xs font-medium rounded-lg"}>
              책 정보
            </button>
            <button type="button" onClick={() => setEditMode("reviewRequest")} className={ (editMode === "base" ? "text-gray-900 hover:bg-gray-300" : "text-white bg-gray-900") + " px-5 py-1.5 text-xs font-medium rounded-lg"}>
              출판검수 신청
            </button>
        </div>
      </div>
  
        { editMode === "base" && 
          <div className="my-16">
            <div>
              {/* title */}
              <div className="sm:col-span-2">
                  <label htmlFor="title" className="block mb-2 text-md font-medium text-gray-900">책 제목</label>
                  <input value={localBook.title !== null ? localBook.title : ''} onChange={(e) => localBookDispatch({type: "TITLE", payload: e.target.value})} type="text" name="title" id="title" className="bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="책 제목을 입력해주세요." />
              </div>
              <br /><br />
              {/* coverImg */}
              <div className="block mb-2 text-md font-medium text-gray-900">책 커버 이미지</div>
              <ImageCropComponent defaultSrc={outline.coverImgUrl} cropedFilename={`book-${bookId}-coverImg`} setFileDate={localBookDispatch} />
            </div>
          </div>
        }
  
        { editMode === "reviewRequest" &&
        <>
          <div className="mt-16">
            <button className="btn btn" onClick={() => {window.location.href = `/viewer/${bookId}`}}>전체 책 미리보기</button>
            <p className="mt-1">위의 전체 책 미리보기 버튼을 클릭하여 전체 책의 Preview를 확인해주세요</p>
          </div>

          <div className="mt-16 text-black">
            <p>미리보기를 모두 확인하셨다면, 아래 버튼을 클릭하여 출판 신청을 해주세요.</p>
            <p>출판 신청이 완료되면, 관리자의 출판검수가 시작됩니다.</p>
            <p>검수가 끝나면 이메일을 보내 알려드리겠습니다.</p>
            <button className="btn btn" onClick={() => {requestBookReview()}}>출판하기</button>
          </div>
        </>
        }
      </>
  );



  function localBookReducer(state : BookMutation, action : {type : string, payload : string | File}) : any {
    switch(action.type){
      case 'TITLE':
        return {
          ...state,
          title: action.payload as string
        }
      case 'COVER_IMG':
        return {
          ...state,
          coverImg: action.payload as File
        }
      default:
        return state;
    }
  }

  // 출판 신청
  function requestBookReview(){
    axios.post(`/api/books/${bookId}/status?status=REVIEW_REQUESTED`)
    .then(res => {
      if(res.data.status === "REVIEW_REQUESTED"){
        window.location.href = `/account/books`;
        localStorage.setItem("alertStorageKey", "success:출판 검수 신청이 완료되었습니다! <br> 검수가 완료되면 이메일을 보내드리겠습니다.");
      } else {
        throw new Error("출판 신청에 실패했습니다.");
      }
    })
    .catch(err => {
      flowAlert("출판 신청에 실패했습니다.");
      console.log(err);
    })
  }


}