import RegionForm from "Frontend/components/region/region-form";
import { useNavigate } from "react-router-dom";
import { Notification } from "@vaadin/react-components";
import { RegionEndpoint } from "Frontend/generated/endpoints";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";

export default function RegionCreateView() {

    const navigate = useNavigate();

    const handleConfirm = async (region: RegionDto) => {
        await RegionEndpoint.create(region);
        Notification.show(`Region '${region.displayName}' created successfully`, {theme: "success", position: "bottom-end", duration: 5000});
        navigateToRegions();
    }

    const navigateToRegions = () => {
        navigate("/regions");
    }

    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-md w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow p-6"}>
                <header className={"flex flex-col sm:flex-row mb-4 sm:rounded-lg backdrop-blur bg-white/75 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <h1 className={"flex-grow text-xl lg:text-3xl leading-10 font-medium dark:text-slate-100 content-center"}>Create new region</h1>
                </header>
                <RegionForm onConfirm={handleConfirm} onCancel={navigateToRegions}/>
            </main>
        </div>
    )
}