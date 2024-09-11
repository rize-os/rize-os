import {useEffect, useState} from "react";
import OrganizationListItem from "Frontend/components/organization/organization-list-item";
import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";
import { OrganizationEndpoint } from "Frontend/generated/endpoints";


export default function HomeView() {

    const [organizations, setOrganizations] = useState<OrganizationDto[]>([]);

    useEffect(() => {
        OrganizationEndpoint.findAll().then(setOrganizations);
    }, []);

    return (
        <div>
            {organizations.map((organization) => (
                <OrganizationListItem organization={organization} key={organization.id}/>
            ))}
        </div>
    );
}

