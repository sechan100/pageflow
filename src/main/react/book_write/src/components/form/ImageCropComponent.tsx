import React, { useRef, useState } from 'react';
import ReactCrop, { Crop } from 'react-image-crop';
import 'react-image-crop/dist/ReactCrop.css';


interface ImageCropComponentProps {
  defaultSrc? : string;
  setCropedCoverImg : React.Dispatch<React.SetStateAction<File | null>>;
}


function ImageCropComponent({defaultSrc, cropedImgFile} : ImageCropComponentProps) {

  // 원본 이미지 src: 최초 props로 받아온게 없다면 default 이미지로 설정
  const [src, setSrc] = useState<string | null>(defaultSrc !== undefined ? defaultSrc : "/img/defaultImg.jpg");

  // 크롭된 이미지 src
  const [croppedImageUrl, setCroppedImageUrl] = useState<string | null>(null);

  // 이미지 ref
  const imageRef = useRef<HTMLImageElement>(null);

  // 저장하기 버튼 ref
  const saveCropStatusBtn = useRef<HTMLButtonElement>(null);

  // 수정하기 버튼 ref
  const modifyCropStatusBtn = useRef<HTMLButtonElement>(null);

  // 이미지 드롭 or 선택 input ref
  const sourceImgInput = useRef<HTMLInputElement>(null);

  // 크롭 상태
  const [crop, setCrop] = useState<Crop>({
    x: 0,
    y: 0,
    width: 11 * 3.5,
    height: 16 * 3.5,
    unit: '%'
  });


  const cropedImgData = useRef({
    savedCrop: crop,
  });




  return (
    <div>
      {src && (
        <ReactCrop
            crop={crop}
            onChange={onCropChange}
            aspect={11 / 16}
          >
          <div className='w-auto overflow-hidden border border-4 border-blue-500'>
            <img src={croppedImageUrl !== null ? croppedImageUrl : src} ref={imageRef} alt="Crop me" />
          </div>
        </ReactCrop>
      )}

      {/* 이미지 드롭 or 선택 input */}
      <div className="flex items-center justify-between items-stretch w-full">
        <label htmlFor="coverImgDropzone" className="flex flex-col items-center justify-center w-full h-28 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:hover:bg-bray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600">
          <div className="flex flex-col items-center justify-center pt-5 pb-6">
            <svg className="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
            </svg>
            <p className="mb-2 text-sm text-gray-500 dark:text-gray-400"><span className="font-semibold">클릭하거나 </span>드래그하여 사진을 업로드</p>
            <p className="text-xs text-gray-500 dark:text-gray-400">11 x 16 비율</p>
          </div>
          <input id="coverImgDropzone" ref={sourceImgInput} accept='image/*' onChange={onSelectFile} type="file"  className="hidden" />
        </label>

        <button type='button' ref={saveCropStatusBtn} onClick={saveCropStatus} className='hidden px-4 ml-5 flex-none text-white bg-blue-500 rounded-lg hover:bg-blue-600'>저장하기</button>
        <button type='button' ref={modifyCropStatusBtn} onClick={modifyCropStatus} className='hidden px-4 ml-5 flex-none text-white bg-blue-500 rounded-lg hover:bg-blue-600'>수정하기</button>
      </div>
      {/* 이미지 드롭 or 선택 input 종료 */}

    </div>
  );



  function onSelectFile(e : any){
    if (e.target.files && e.target.files.length > 0) {
      const reader = new FileReader();
      reader.addEventListener('load', () => setSrc(reader.result as string));
      reader.readAsDataURL(e.target.files[0]);
    }
    saveCropStatusBtn.current?.classList.toggle('hidden');
  };

  function onCropChange(newCrop : any){
    setCrop(newCrop);
  };

  function saveCropStatus(){
    if (imageRef.current && crop.width && crop.height) {
      const croppedImageUrl = getCroppedImg(imageRef.current, crop);
      setCroppedImageUrl(croppedImageUrl);
      cropedImgData.current.savedCrop = crop;
      setCrop({
        x: 0,
        y: 0,
        width: 0,
        height: 0,
        unit: '%'
      });
      saveCropStatusBtn.current?.classList.toggle('hidden');
      modifyCropStatusBtn.current?.classList.toggle('hidden');
      setCropedCoverImg(dataURLtoFile(croppedImageUrl, 'coverImg.jpg')
    }
  };

  function modifyCropStatus(){
    setCroppedImageUrl(null);
    setCrop(cropedImgData.current.savedCrop);
    saveCropStatusBtn.current?.classList.toggle('hidden');
    modifyCropStatusBtn.current?.classList.toggle('hidden');
  }

  function getCroppedImg(image: HTMLImageElement, crop: Crop) {
    const canvas = document.createElement('canvas');
    const scaleX = image.naturalWidth / image.width;
    const scaleY = image.naturalHeight / image.height;
    
    // 캔버스의 크기를 원본 이미지의 크기로 설정
    canvas.width = image.naturalWidth;
    canvas.height = image.naturalHeight;
    
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      return '';
    }

    // 원본 이미지를 캔버스에 그림
    ctx.drawImage(image, 0, 0, image.naturalWidth, image.naturalHeight);
    
    // 어두운 오버레이를 추가하기 위한 설정
    ctx.fillStyle = 'rgba(0, 0, 0, 0.8)'; // 반투명 검은색
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    
    // 크롭 영역 내에서만 원본 이미지를 그려넣음 (어두운 오버레이를 '지움')
    ctx.globalCompositeOperation = 'destination-out';
    ctx.drawImage(
      image,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY
    );
  
    // 크롭 영역 내 이미지를 복원
    ctx.globalCompositeOperation = 'source-over';
    ctx.drawImage(
      image,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY
    );
  
    // 크롭된 이미지를 data URL 형태로 반환
    return canvas.toDataURL('image/jpeg');
  };

  function dataURLtoFile(dataUrl : any, filename : string){

    // dataURL의 내용을 분리하여 MIME 타입과 데이터 부분을 추출합니다.
    let arr = dataUrl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
  
    // Uint8Array를 사용하여 바이너리 데이터를 생성합니다.
    while(n--){
        u8arr[n] = bstr.charCodeAt(n);
    }
  
    // Blob 객체를 생성하여 파일로 반환합니다.
    return new File([u8arr], filename, {type:mime});
  }
  
  
}

export default ImageCropComponent;
