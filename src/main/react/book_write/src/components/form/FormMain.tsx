import { Route, Routes } from "react-router-dom";
import BookForm from "./pages/BookForm";
import MutationSaveBtn from "./MutationSaveBtn";
import ChapterForm from "./pages/ChapterForm";
import { Outline } from "../../types/types";



export default function FormPage() {


  return (
    <main className="flex-auto relative">
      <MutationSaveBtn />
      <Routes>
        <Route path="/" element={<BookForm />}></Route>
        <Route path="/chapter/:chapterId" element={<ChapterForm />}></Route>
        {/* <Route path="/page/:pageId" element={<BookForm {...props}/>}></Route> */}
      </Routes>
    </main>
  );
}
