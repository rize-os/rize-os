interface LoadingRingProps {
    className?: string
}

export default function LoadingRing({ className }: LoadingRingProps) {
    return (
        <div className={"loading-ring " + className}/>
    );
}