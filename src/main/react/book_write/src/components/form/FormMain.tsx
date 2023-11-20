import { Route, Routes } from "react-router-dom";
import BookForm from "./pages/book/BookForm";
import MutationSaveBtn from "../saveBtn/MutationSaveBtn";
import ChapterForm from "./pages/chapter/ChapterForm";
import { Outline } from "../../types/types";
import PageForm from "./pages/page/PageForm";



export default function FormPage() {


  return (
    <main className="flex-auto relative">
      <MutationSaveBtn />
      <Routes>
        <Route path="/" element={<BookForm />}></Route>
        <Route path="/chapter/:chapterId" element={<ChapterForm />}></Route>
        <Route path="/chapter/:chapterId/page/:pageId" element={<PageForm />}></Route>
      </Routes>
    </main>
  );
}
