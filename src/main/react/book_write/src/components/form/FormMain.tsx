import { Route, Routes } from "react-router-dom";
import ChapterForm from "./pages/ChapterForm";
import BookForm from "./pages/BookForm";



export default function FormPage() {


  return (
    <main className="flex-auto relative">
      <Routes>
        <Route path="/" element={<BookForm />}></Route>
        <Route path="/chapter/:chapterId" element={<ChapterForm />}></Route>
        {/* <Route path="/page/:pageId" element={<BookForm {...props}/>}></Route> */}
      </Routes>
    </main>
  );
}
