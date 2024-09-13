import { useNavigate } from 'react-router-dom';
import { useEffect } from "react";

export default function HomeView() {

    const navigate = useNavigate();
    useEffect(() => {
        navigate("/organizations");
    }, []);
    return (<></>);
}

