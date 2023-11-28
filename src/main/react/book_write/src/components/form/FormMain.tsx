import { Route, Routes } from "react-router-dom";
import BookForm from "./pages/book/BookForm";
import MutationSaveBtn, { useAutoSaveStore } from "../saveBtn/MutationSaveBtn";
import ChapterForm from "./pages/chapter/ChapterForm";
import PageForm from "./pages/page/PageForm";
import { useEffect } from "react";
import _ from "lodash";



export default function FormPage() {

  // 자동저장 비활성화 -> 타이핑시 자동저장 off
  const {isAutoSaveAvailable} = useAutoSaveStore();

  // 타이핑 이벤트를 등록하여 타이핑시에는 자동저장을 비활성화 시킨다.
  // 타이핑 이벤트가 발생하면 isAutoSaveAvailable.current를 false로 변경하고, 
  // 마지막 타이핑이 종료되고 1초 후에 isAutoSaveAvailable.current를 true로 변경하도록 디바운스를 건다.
  useEffect(() => {
    if(isAutoSaveAvailable.current){
      const typingEvent = () => {
        isAutoSaveAvailable.current = false;
      }
      document.addEventListener('keydown', typingEvent);
      document.addEventListener('keyup', _.debounce(() => {
        isAutoSaveAvailable.current = true;
      }, 1000));
      return () => {
        document.removeEventListener('keydown', typingEvent);
        document.removeEventListener('keyup', _.debounce(() => {
          isAutoSaveAvailable.current = true;
        }, 1000));
      }
    }
  }, [isAutoSaveAvailable]);


  return (
    <main className="flex-auto relative px-[7%]">
      <MutationSaveBtn />
      <Routes>
        <Route path="/" element={<BookForm />}></Route>
        <Route path="/chapter/:chapterId" element={<ChapterForm />}></Route>
        <Route path="/chapter/:chapterId/page/:pageId" element={<PageForm />}></Route>
      </Routes>
    </main>
  );
}
