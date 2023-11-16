/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useRef, useState } from 'react';
import { useGetOutline } from '../../api/book-apis';
import { Outline } from '../../types/types';
import MutationSaveBtn from './SaveMutationBtn';
import { useUpdateBook } from '../../api/book-apis';
import ImageCropComponent from './ImageCropComponent';



interface BookFormProps {
  bookId : number;
  queryClient : any;
}

export default function BookForm(props : BookFormProps){

  const { bookId, queryClient } = props;
  const outline : Outline = useGetOutline(bookId);
  const isUpdated = useRef(false); // 실제로 데이터가 업데이트 되었는지를 기록, 불필요한 서버 통신을 사전에 막는다.
  const [saveActive, setSaveActive] = useState(false); // Save 버튼의 클릭 상태를 상위 컴포넌트로 끌어올리기 위한 state
  const coverImgPreview = useRef<HTMLImageElement>(null); // coverImg의 미리보기를 위한 ref
  const coverImgInput = useRef<HTMLInputElement>(null); // coverImg의 input을 위한 ref

  const [bookMutation, setBookMutation] = useState({
    title: outline.title,
    coverImg: null
  });

  const { mutateAsync, isLoading, error } = useUpdateBook(bookId);


  // outline 데이터의 변경시, 이미 선언된 state인 bookMutation의 상태를 업데이트하기 위함
  useEffect(() => {
    setBookMutation({
      title: outline.title,
      coverImg: null
    });
  }, [outline]);



  useEffect(() => {

    // 업데이트가 되었다면 서버에 요청을 보낸다.
    if(isUpdated.current){
      mutateAsync(bookMutation);
      isUpdated.current = false; // 초기화
    }


  }, [saveActive]);


  
  return (
    <>
      <MutationSaveBtn setSaveActive={setSaveActive} isUpdated={isUpdated} />
      <div className="px-24 mt-16">

        {/* title */}
        <div className="sm:col-span-2">
            <label htmlFor="title" className="block mb-2 text-sm font-medium text-gray-900">책 체목</label>
            <input value={bookMutation.title} onChange={handleBookTitle} onKeyDown={handleTitleInputEnterPress} type="text" name="title" id="title" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="책 제목을 입력해주세요." />
        </div>

        {/* coverImg */}
        {/* <div className='aspect-ratio-11-16'>
          <img ref={coverImgPreview} src="se" alt='' className='w-1/2'></img>
        </div>
        <div className="flex items-center justify-center w-full">
          <label htmlFor="coverImgDropzone" className="flex flex-col items-center justify-center w-full h-28 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:hover:bg-bray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600">
            <div className="flex flex-col items-center justify-center pt-5 pb-6">
              <svg className="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
              </svg>
              <p className="mb-2 text-sm text-gray-500 dark:text-gray-400"><span className="font-semibold">클릭하거나 </span>드래그하여 사진을 업로드</p>
              <p className="text-xs text-gray-500 dark:text-gray-400">11 x 16 비율</p>
            </div>
            <input id="coverImgDropzone" accept='image/*' onChange={readAndShowCoverImgFile} ref={coverImgInput} type="file"  className="hidden" />
          </label>
        </div> */}
        <ImageCropComponent />



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


  function readAndShowCoverImgFile(){
      const preview = coverImgPreview.current;
      if(!preview) return;
      const coverImg = coverImgInput.current?.files?.[0];

      const reader = new FileReader();
      reader.onloadend = function () {
          preview.src = reader.result as string;
      }

      if (coverImg) {
          reader.readAsDataURL(coverImg);
      } else {
          preview.classList.add("hidden");
      }
  }
}