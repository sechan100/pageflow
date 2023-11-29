/* eslint-disable react-hooks/exhaustive-deps */
import React, { useContext, useEffect, useMemo } from "react";
import ReactQuill from "react-quill";
import 'react-quill/dist/quill.snow.css';
import { QueryContext } from "../../../../App";
import { useParams } from "react-router-dom";
import axios from "axios";
import { create } from "zustand";


export const useQuillFocusStore = create((set : any) => ({
  reserveFocus: false,
  quillFocus: () => set({ reserveFocus: true }),
  resetQuillFocus: () => set({ reserveFocus: false }),
}));


export default function QuillEditor({quillValue, setContent, viewPortHeight} : {quillValue : string, setContent : any, viewPortHeight : number}) {

  const {bookId} = useContext(QueryContext);
  const {pageId} = useParams();
  const {reserveFocus, resetQuillFocus} = useQuillFocusStore();
  const quillRef = React.useRef<ReactQuill>(null);

  const modules =  useMemo(() => ({
    toolbar: {
      container: [
        [{ header: [1, 2, 3, false] }],
        ["bold", "italic"],
        [{"indent": "-1"}, {"indent": "+1"}],
        ["image"],
      ],
      handlers: { image: imgHandler },
    },
  }), []);

  useEffect(() => {
    if(quillRef.current && reserveFocus){
      const editor = quillRef.current.getEditor();
      editor.focus();
      editor.setSelection(editor.getLength(), 0);
      resetQuillFocus();
    }
  }, [quillValue]);


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
        className={`h-[${viewPortHeight}vh] text-lg racking-wide leading-loose`}
      />
    </>
  );


  function imgHandler(){
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
          editor.insertEmbed(range.index, "image", `/img/loading.gif`);
  
          const formData = new FormData();
          formData.append('imgFile', file);
    
          const res = await axios.post(`/api/books/${bookId}/chapters/pages/${pageId}/img`, formData, {
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
