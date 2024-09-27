import {useNavigate, useParams} from 'react-router-dom';
import RegionForm from "Frontend/components/region/region-form";
import RegionDto from "Frontend/generated/rize/os/commons/region/RegionDto";
import {useEffect, useState} from "react";
import {RegionEndpoint} from "Frontend/generated/endpoints";
import {Notification} from "@vaadin/react-components";

export default function OrganizationEditView() {

    const navigate = useNavigate();
    const { regionId } = useParams();

    const [region, setRegion] = useState<RegionDto | undefined>(undefined);

    const fetchRegion = (regionId: string) => {
        RegionEndpoint.findById(regionId)
            .then(setRegion)
            .catch(navigateToRegions);
    }

    const handleConfirm = async (region: RegionDto) => {
        await RegionEndpoint.update(region);
        Notification.show(`Region '${region.displayName}' saved successfully`, {theme: "success", position: "bottom-end", duration: 5000});
        navigateToRegions();
    }

    const navigateToRegions = () => {
        navigate("/regions");
    }

    useEffect(() => {
        if (regionId)
            return fetchRegion(regionId);

        navigateToRegions();
    }, [regionId]);

    return (
        <div className={"flex w-full justify-center sm:p-6"}>
            <main className={"max-w-md w-full bg-white dark:bg-slate-900 sm:rounded-lg shadow p-6"}>
                <header className={"flex flex-col sm:flex-row mb-4 sm:rounded-lg backdrop-blur bg-white/75 dark:bg-slate-900/80 sticky top-0 z-10"}>
                    <h1 className={"flex-grow text-xl lg:text-3xl leading-10 font-medium dark:text-slate-100 content-center"}>Edit organization</h1>
                </header>
                <RegionForm region={region} onConfirm={handleConfirm} onCancel={navigateToRegions}/>
            </main>
        </div>
    )
}