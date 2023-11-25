import React, { useContext, useMemo } from "react";
import ReactQuill from "react-quill";
import 'react-quill/dist/quill.snow.css';
import { QueryContext } from "../../../../App";
import { useParams } from "react-router-dom";
import axios from "axios";

export default function QuillEditor({quillValue, setContent, viewPortHeight} : {quillValue : string, setContent : any, viewPortHeight : number}) {

  const {bookId} = useContext(QueryContext);
  const {pageId} = useParams();
  const quillRef = React.useRef<ReactQuill>(null);

  const modules =  useMemo(() => ({
    toolbar: {
      container: [
        [{ header: [1, 2, 3, false] }],
        ["bold", "italic"],
        [{"indent": "-1"}, {"indent": "+1"}],
        ["image"],
      ],
      handlers: { image: imgHandler(bookId, Number(pageId)) },
    },
  }), [bookId, pageId]);

  const formats = [
    "header",
    "bold",
    "italic",
    "indent",
    "image",
  ];

  return (
    <>
      <ReactQuill
        theme="snow"
        ref={quillRef}
        modules={modules}
        formats={formats}
        value={quillValue}
        onChange={setContent}
        preserveWhitespace
        className={`h-[${viewPortHeight}vh] text-lg racking-wide leading-loose`}
      />
    </>
  );


  function imgHandler(bookId: number, pageId: number){
    return () => {
      try {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();
      
        input.onchange = async () => {
          if(quillRef.current){
            const file = (input.files as FileList)[0];
  
            const editor = quillRef.current.getEditor(); 
            const range = editor.getSelection();
            if(!range) return;
  
            // 서버에 올려질때까지 표시할 로딩 placeholder 삽입                
            editor.insertEmbed(range.index, "image", `/img/unloaded_img.jpg`);
    
            const formData = new FormData();
            formData.append('imgFile', file);
      
            const res = await axios.post(`/api/book/${bookId}/chapter/page/${pageId}/img`, formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            });
            
            if(res.data){
              const imgUrl = res.data;
              editor.deleteText(range.index, 1);
              editor.insertEmbed(range.index, "image", imgUrl);
              editor.setSelection(range.index + 1, 1);
              
            }
          }
        }
  
      } catch (error) {
        console.log(error);
      }
    }
  } 



}
