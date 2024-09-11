import OrganizationDto from "Frontend/generated/rize/os/commons/organization/OrganizationDto";

interface OrganizationListItemProps {
    organization: OrganizationDto
}

const OrganizationListItem: React.FC<OrganizationListItemProps> = ({ organization }) => {
    return (
        <main className={"flex flex-row p-1 gap-2"}>
            <div className={"bg-slate-200 rounded-lg grid items-center justify-items-center w-10 h-10"}>
                <span className="material-icons text-slate-500 text-3xl">apartment</span>
            </div>

            <div className={"flex flex-col"}>
                <span className={"leading-5"}>{organization.displayName}</span>
                <span className={"text-xs leading-4"}>{organization.name}</span>
            </div>
        </main>
    );
}

export default OrganizationListItem;