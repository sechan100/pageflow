/* eslint-disable react-hooks/exhaustive-deps */

import { useContext, useEffect, useRef, useState } from "react";
import { QueryContext } from "../../../App";
import { useGetOutlineQuery } from "../../../api/outline-api";
import { Outline } from "../../../types/types";
import { useUpdateBook } from "../../../api/book-api";
import  MutationSaveBtn  from "../MutationSaveBtn";
import ImageCropComponent from "../BookCoverImgCropper";
import flowAlert from "../../../etc/flowAlert";




export default function BookForm(){

  const { bookId } = useContext(QueryContext);
  const outline : Outline = useGetOutlineQuery(bookId);
  const isUpdated = useRef(false); // 실제로 데이터가 업데이트 되었는지를 기록, 불필요한 서버 통신을 사전에 막는다.
  const [saveActive, setSaveActive] = useState(false); // Save 버튼의 클릭 상태를 상위 컴포넌트로 끌어올리기 위한 state
  const [coverImg, setCoverImg] = useState<File | null>(null); // coverImg의 파일을 저장하기 위한 state
  const [bookMutation, setBookMutation] = useState<{title:string; coverImg:File | null}>({
    title: outline.title,
    coverImg: null
  });

  const [mutateAsync, isLoading, isError] = useUpdateBook(bookId);


  // outline 데이터의 변경시, 이미 선언된 state인 bookMutation의 상태를 업데이트하기 위함
  useEffect(() => {
    if(outline){
      setBookMutation({
        title: outline.title,
        coverImg: null
      });
    }
  }, [outline]);

  
  useEffect(() => {
    if(coverImg !== null){
      setBookMutation((prev) => ({
        ...prev,
        coverImg: coverImg
      }));
      isUpdated.current = true;
    }
  }, [coverImg]);


  useEffect(() => {
    // 업데이트가 되었다면 서버에 요청을 보낸다.
    if(isUpdated.current){
      mutateAsync(bookMutation);
    }
  }, [saveActive]);

  // 업데이트가 완료되면 알림을 띄우고 isUpdated를 초기화한다.
  useEffect(() => {
    if(!isLoading && isUpdated.current){
      isUpdated.current = false; // 초기화
      if(isError){
        flowAlert("error", "서버에 데이터를 저장하지 못했습니다. <br> 잠시후에 다시 시도해주세요.");
        return;
      }
      flowAlert("success", "책 정보가 업데이트 되었습니다.");
    }
  }, [isLoading]);


  return (
    <>
      <MutationSaveBtn setSaveActive={setSaveActive} isUpdated={isUpdated} />
      <div className="px-24 mt-16">
        {/* title */}
        <div className="sm:col-span-2">
            <label htmlFor="title" className="block mb-2 text-md font-medium text-gray-900">책 체목</label>
            <input value={bookMutation.title} onChange={handleBookTitle} onKeyDown={handleTitleInputEnterPress} type="text" name="title" id="title" className="bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="책 제목을 입력해주세요." />
        </div>
        <br /><br />
        <div className="block mb-2 text-md font-medium text-gray-900">책 커버 이미지</div>
        {/* coverImg */}
        <ImageCropComponent defaultSrc={outline.coverImgUrl} cropedFilename={`book-${bookId}-coverImg`} setFileDate={setCoverImg} />
      </div>
    </>
  );



  function handleBookTitle(e : React.ChangeEvent<HTMLInputElement>) {
    setBookMutation(
      (prev) => ({
        ...prev,
        title: e.target.value
      })
    );

    // 기존 업데이트 전의 데이터와 비교하여 다르다면 isUpdated를 true로 변경
    if(e.target.value !== outline.title){
      isUpdated.current = true;

    // 중간에 변경되었더라도, 다시 원래대로 돌아온 경우, isUpdated를 false로 변경  
    } else {
      isUpdated.current = false;
    }
  }

  function handleTitleInputEnterPress(e : React.KeyboardEvent<HTMLInputElement>) {
    if(e.key === 'Enter' && isUpdated.current){
      setSaveActive((prev) => (!prev));
    }
  }

}