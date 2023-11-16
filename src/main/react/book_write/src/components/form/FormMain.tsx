import { Route, Routes } from "react-router-dom";
import ChapterForm from "./ChapterForm";
import BookForm from "./BookForm";
import { useRef, useState } from "react";





interface formPageProps {
  bookId : number;
  queryClient : any;
}


export default function FormPage(props : formPageProps) {


  return (
    <main className="flex-auto relative">
      <Routes>
        <Route path="/" element={<BookForm {...props} />}></Route>
        <Route path="/chapter/:chapterId" element={<ChapterForm {...props}/>}></Route>
        {/* <Route path="/page/:pageId" element={<BookForm {...props}/>}></Route> */}
      </Routes>
    </main>
  );
}
