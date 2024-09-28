import React from "react";

interface ImageUploadProps {
    placeholder: React.ReactElement,
    imageUrl?: string,
    className?: string
}

const ImageUpload: React.FC<ImageUploadProps> = ({ placeholder, imageUrl, className }) => {

    const refinedPlaceholder = () => {
        return React.cloneElement(placeholder, { className: `${placeholder.props.className} rounded-xl col-start-1 row-start-1`});
    }

    return (
        <div className={`grid cursor-pointer h-min w-min select-none ${className}`}>
            { !imageUrl && refinedPlaceholder() }
            <div className={"grid place-items-center col-start-1 row-start-1 rounded-xl bg-slate-700/30 opacity-0 hover:opacity-100 backdrop-blur-sm transition-all duration-[400ms]"}>
                <div className={"flex flex-col items-center text-slate-50"}>
                    <span className={"material-icons text-3xl sm:text-6xl"}>upload</span>
                    <span className={"text-xs sm:text-base"}>Upload Image</span>
                </div>
            </div>
        </div>
    )
}

export default ImageUpload;
