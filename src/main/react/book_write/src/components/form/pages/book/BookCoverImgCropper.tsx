/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useRef, useState } from 'react';
import ReactCrop, { Crop } from 'react-image-crop';
import 'react-image-crop/dist/ReactCrop.css';
import { useAutoSaveStore } from '../../../saveBtn/MutationSaveBtn';


interface ImageCropComponentProps {
  cropedFilename : string;
  defaultSrc? : string;
  setFileDate : any;
}


function ImageCropComponent({cropedFilename, defaultSrc, setFileDate} : ImageCropComponentProps) {

  // 원본 이미지 src: 최초 props로 받아온게 없다면 default 이미지로 설정
  const [src, setSrc] = useState<string>(defaultSrc !== undefined ? defaultSrc : "/img/unloaded_img.jpg");
  const {isAutoSaveAvailable} = useAutoSaveStore();


  // props로 받아온 defaultSrc가 변경되면 src도 변경
  useEffect(() => {
    setSrc(defaultSrc !== undefined ? defaultSrc : "/img/unloaded_img.jpg");
  }, [defaultSrc]);

  // 크롭된 이미지 src
  const [croppedImageSrc, setCroppedImageSrc] = useState<string | null>(null);

  // 이미지 ref
  const imageRef = useRef<HTMLImageElement>(null);

  // 수정상태 여부
  const [isModifyMode, setIsModifyMode] = useState<boolean>(false);


  // 편집 상태일 때, 자동저장 off
  useEffect(() => {
    if(isModifyMode){
      isAutoSaveAvailable.current = false;
    } else {
      isAutoSaveAvailable.current = true;
    }
  }, [isModifyMode]);

  // 이미지 드롭 or 선택 input ref
  const sourceImgInput = useRef<HTMLInputElement>(null);

  // 크롭 상태
  const [crop, setCrop] = useState<Crop>({
    x: 0,
    y: 0,
    width: 20 * (11 / 16),
    height: 20,
    unit: 'px'
  });



  const cropedImgData = useRef({
    savedCrop: crop,
  });




  return (
    <div className='w-1/3'>
      {isModifyMode && 
      <div className='flex justify-center border border-4 border-blue-500 p-5 bg-gray-100'>
        <ReactCrop
            crop={crop}
            onChange={onCropChange}
            aspect={11 / 16}
            ruleOfThirds={true}
          >
          <div className='w-auto overflow-hidden'>
            <img src={croppedImageSrc !== null ? croppedImageSrc : src} ref={imageRef} alt="Crop me" />
          </div>
        </ReactCrop>
      </div>}

      {!isModifyMode && 
        <div className='w-auto flex justify-center overflow-hidden border border-4 border-blue-500 p-5 bg-gray-100'>
          <img src={croppedImageSrc !== null ? croppedImageSrc : src} ref={imageRef} alt="Crop me" />
        </div>}

      {/* 이미지 드롭 or 선택 input */}
      <div className="flex items-center justify-between items-stretch w-full h-28 mt-1">

        {isModifyMode && 
        <label htmlFor="coverImgDropzone" className="flex flex-col mr-5 items-center justify-center w-full h-28 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:hover:bg-bray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600">
          <div className="flex flex-col items-center justify-center pt-5 pb-6">
            <svg className="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
              <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
            </svg>
            <p className="mb-2 text-xs text-gray-500 dark:text-gray-400"><span className="font-semibold">클릭하거나 드래그하여</span><br />사진을 업로드</p>
            <p className="text-xs text-gray-500 dark:text-gray-400">11 x 16 비율</p>
          </div>
          <input id="coverImgDropzone" ref={sourceImgInput} accept='image/*' onChange={onSelectFile} type="file" className="hidden" />
        </label>}

        {isModifyMode && <button type='button' onClick={saveCropStatus} className='px-4 flex-initial w-full text-white bg-blue-500 rounded-lg hover:bg-blue-600'>저장하기</button>}
        {!isModifyMode && <button type='button' onClick={modifyCropStatus} className='px-4 flex-initial w-full text-white bg-blue-500 rounded-lg hover:bg-blue-600'>수정하기</button>}
      </div>
      {/* 이미지 드롭 or 선택 input 종료 */}

    </div>
  );




  // 이미지 드롭 or 선택시
  function onSelectFile(e : any){
    if (e.target.files && e.target.files.length > 0) {
      const reader = new FileReader();
      reader.addEventListener('load', () => setSrc(reader.result as string));
      reader.readAsDataURL(e.target.files[0]);
    }
  };


  // 크롭 상태 변경시
  function onCropChange(newCrop : any){
    setCrop(newCrop);
  };


  // 저장하기 버튼 클릭시
  function saveCropStatus(){
    if (imageRef.current && crop.width && crop.height) {

      // [크롭 영역이 아닌 곳은 어두운 오버레이가 적용된 사진, 크롭 영역만 포함한 사진]
      const croppedImageSrc  = getCroppedImg(imageRef.current, crop);
      setCroppedImageSrc(croppedImageSrc);
      cropedImgData.current.savedCrop = crop;
      setCrop({
        x: 0,
        y: 0,
        width: 0,
        height: 0,
        unit: '%'
      });
      setIsModifyMode((prev) => !prev);

      
      const cropedCoverImg : File = dataURLtoFile(croppedImageSrc);
      setFileDate({type: "COVER_IMG", payload: cropedCoverImg});
    }
  };


  // 수정하기 버튼 클릭시
  function modifyCropStatus(){
    setCroppedImageSrc(null);
    setCrop(cropedImgData.current.savedCrop);
    setIsModifyMode((prev) => !prev);

  }


  // 이미지 크롭 함수
  function getCroppedImg(image: HTMLImageElement, crop: Crop){
    const canvas = document.createElement('canvas');
    const scaleX = image.naturalWidth / image.width;
    const scaleY = image.naturalHeight / image.height;
    canvas.width = crop.width;
    canvas.height = crop.height;
    const ctx = canvas.getContext('2d');

    ctx?.drawImage(
      image,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY,
      0,
      0,
      crop.width,
      crop.height
    );

    return canvas.toDataURL('image/jpeg');
  }; 
  


  // dataURL을 File 객체로 변환
  function dataURLtoFile(dataUrl : any){

    // dataURL의 내용을 분리하여 MIME 타입과 데이터 부분을 추출합니다.
    let arr = dataUrl.split(",");
    let mime = arr[0].match(/:(.*?);/)[1];
    let bstr = atob(arr[1]);
    let n = bstr.length;
    let u8arr = new Uint8Array(n);
  
    // Uint8Array를 사용하여 바이너리 데이터를 생성합니다.
    while(n--){
        u8arr[n] = bstr.charCodeAt(n);
    }
  
    // Blob 객체를 생성하여 파일로 반환합니다.
    return new File([u8arr], cropedFilename + ".jpeg", {type:mime});
  }
  
}

export default ImageCropComponent;
