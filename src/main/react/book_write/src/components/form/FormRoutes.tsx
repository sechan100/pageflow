import { Route, Routes } from "react-router-dom";
import BookForm from "./BookForm";
import ChapterForm from "./ChapterForm";





interface formPageProps {
  bookId : number;
  queryClient : any;
}

export default function FormPage(props : formPageProps) {


  return (
    <Routes>
      <Route path="/" element={<BookForm {...props}/>}></Route>
      <Route path="/chapter/:chapterId" element={<ChapterForm {...props}/>}></Route>
      <Route path="/page/:pageId" element={<BookForm {...props}/>}></Route>
    </Routes>
  );
}