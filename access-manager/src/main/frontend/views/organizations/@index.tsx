import { useEffect, useState } from 'react';
import { OrganizationEndpoint } from "Frontend/generated/endpoints";
import OrganizationItem from "Frontend/components/organization-item";
import Organization from "Frontend/generated/rize/os/access/manager/organization/Organization";

export default function OrganizationView()
{
    const [organizations, setOrganizations] = useState<Organization[]>([]);

    useEffect(() => {
        OrganizationEndpoint.findAll().then(setOrganizations);
    }, []);

    return (
        <div className={"flex flex-col h-full"}>
            <header className={"p-m box-border"}>
                <h2>Your Organizations</h2>
            </header>
            <main style={{flex: 1, "overflow-y": "auto"}}>
                <div className={"grid gap-m px-m py-xs box-border"} style={{"grid-template-columns": "repeat(auto-fill, minmax(24rem, 1fr))"}}>
                    {organizations.map((organization) => (
                        <OrganizationItem key={organization.id} organization={organization} className={"box-border"} />
                    ))}
                </div>
            </main>
        </div>
    );
}
