import React from "react";
import ReactQuill from "react-quill";
import 'react-quill/dist/quill.snow.css';

export default function QuillEditor({quillValue, setContent, viewPortHeight} : {quillValue : string, setContent : any, viewPortHeight : number}) {

  const modules = {
    toolbar: [
      [{ header: [1, 2, 3, false] }],
      ["bold", "italic"],
      [{"indent": "-1"}, {"indent": "+1"}],
      ["image"],
    ],
  };

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
        modules={modules}
        formats={formats}
        value={quillValue}
        onChange={setContent}
        preserveWhitespace
        className={`h-[${viewPortHeight}vh] text-lg racking-wide leading-loose`}
      />
    </>
  );
}
