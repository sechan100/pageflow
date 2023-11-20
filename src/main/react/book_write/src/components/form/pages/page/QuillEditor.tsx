import React, { useEffect, useState } from "react";
import ReactQuill from "react-quill";
import 'react-quill/dist/quill.snow.css';

export default function QuillEditor() {
  const [quillValue, setQuillValue] = useState("");

  useEffect(() => {
    console.log(quillValue);
  }, [quillValue]);

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
        onChange={setQuillValue}
        preserveWhitespace
        className="h-1/2 overflow-y-auto"
      />
      <div className="border border-8 border-gray-800" dangerouslySetInnerHTML={{__html: quillValue}}></div>
    </>
  );
}
