/* eslint-disable react-hooks/exhaustive-deps */

import { useContext, useEffect, useReducer, useState } from "react";
import { QueryContext } from "../../../App";
import { useGetOutlineQuery } from "../../../api/outline-api";
import { BookMutation, Outline } from "../../../types/types";
import ImageCropComponent from "../BookCoverImgCropper";
import { create } from "zustand";

interface BookMutationStore {
  payload: BookMutation,
  isMutated: boolean,
  isLoading: boolean,
  dispatchs: {
    setTitle : (title : string) => void,
    setCoverImg : (coverImg : File) => void
  }
}

export const useBookMutationStore = create<BookMutationStore>((set : any) => ({
  payload: { title: null, coverImg: null }, // 책 정보 변경사항을 서버에 요청하기 위한 객체
  isMutated: false, // 변경사항이 존재하는지 여부
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
}));



export default function BookForm(){

  const { bookId } = useContext(QueryContext);
  const outline : Outline = useGetOutlineQuery(bookId);
  const bookStore = useBookMutationStore();
  const [localBook, localBookDispatch] : [BookMutation, any] = useReducer(localBookReducer, bookStore.payload); // zustand store에 변경사항을 업데이트하기 전에 임시로 저장하는 로컬 상태


  // outline 데이터의 변경시, 이미 선언된 state인 bookMutation의 상태를 업데이트하기 위함
  useEffect(() => {
    if(outline){
      localBookDispatch({type: 'TITLE', payload: outline.title});
    }
  }, [outline]);

  
  // 로컬 데이터의 변경사항을 zustand store에 업데이트
  useEffect(() => {

    // local의 title 데이터가 존재하면서 outline의 title 데이터와 다를 경우 => title 변경사항이 존재
    if(localBook.title && isTitleChanged(localBook.title)) bookStore.dispatchs.setTitle(localBook.title);
    
    // local의 coverImg 데이터가 null이 아니라면 => coverImg 변경사항이 존재
    if(localBook.coverImg) bookStore.dispatchs.setCoverImg(localBook.coverImg);

  }, [localBook]);


  return (
    <>
      <div className="px-24 mt-16">
        {/* title */}
        <div className="sm:col-span-2">
            <label htmlFor="title" className="block mb-2 text-md font-medium text-gray-900">책 체목</label>
            <input value={localBook.title !== null ? localBook.title : ''} onChange={(e) => localBookDispatch({type: "TITLE", payload: e.target.value})} type="text" name="title" id="title" className="bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="책 제목을 입력해주세요." />
        </div>
        <br /><br />
        {/* coverImg */}
        <div className="block mb-2 text-md font-medium text-gray-900">책 커버 이미지</div>
        <ImageCropComponent defaultSrc={outline.coverImgUrl} cropedFilename={`book-${bookId}-coverImg`} setFileDate={localBookDispatch} />
      </div>
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

  // 현재 서버 상태에 저장된 데이터와 다른지...
  function isTitleChanged(title : string) : boolean {
    return outline.title !== title;
  }
}