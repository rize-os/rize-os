import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Avatar, MenuBar, Tooltip, MenuBarItemSelectedEvent } from '@vaadin/react-components';
import { Apartment, MoreVert, Edit } from "@mui/icons-material";
import { UserEndpoint } from "Frontend/generated/endpoints";
import type User from "Frontend/generated/rize/os/access/manager/user/User";
import type Organization from "Frontend/generated/rize/os/access/manager/organization/Organization.js";

interface OrganizationItemProps {
    organization: Organization,
    onEdit?: (organization: Organization) => void
}

const separatorStyle = {
    height: "1px",
    width: "100%"
};

const menuBarItems = [
    { component: <MoreVert/>,
        className: "text-gray-500 rounded-full p-0 m-0",
        theme: "icon tertiary",
        children: [
            { component: <><Edit style={{transform: "scale(0.8)"}}/><p className={"mx-2"}>Edit</p></>, key: "edit"}
        ]
    }
]

const OrganizationItem: React.FC<OrganizationItemProps> = ({ organization, onEdit}) => {

    const navigate = useNavigate();

    const [users, setUsers] = useState<User[]>([]);
    const [userCount, setUserCount] = useState<number>(0);

    const fetchUsers = async () => {
        setUsers(await UserEndpoint.findUsersForOrganization(organization.id, 0, 3));
        setUserCount(await UserEndpoint.getUserCountForOrganization(organization.id));
    }

    useEffect(() => {
        fetchUsers().then();
    }, []);

    const handleMenuItemSelected = (e: MenuBarItemSelectedEvent) => {
        // @ts-ignore
        if (e.detail.value.key === "edit")
            onEdit?.(organization);
    }

    return (
        <div className={"border border-contrast-10 rounded-xl flex flex-col box-border "}>
            <section className={"flex flex-row gap-3.5 justify-center p-4 items-center"}>
                <div className={"flex"}>
                    <div className={"grid bg-gray-300 rounded-lg w-14 h-14 content-center justify-center"}>
                        <Apartment className={"text-gray-500"} style={{transform: "scale(1.6)"}}/>
                    </div>
                </div>
                <div className={"flex-grow flex flex-col"}>
                    <span className={"text-xl text-gray-700 font-bold leading-6"}>{organization.displayName}</span>
                    <span className={"text-sm text-gray-400 leading-5"}>{organization.name}</span>
                </div>
                <div>
                    <MenuBar theme="icon" items={menuBarItems} onItemSelected={handleMenuItemSelected}/>
                </div>
            </section>

            <div className={"bg-gray-200"} style={separatorStyle}/>

            <section className={"flex px-4 py-3.5 items-center"}>
                <div className={"flex whitespace-nowrap grow h-min w-full justify-items-start"}>
                    {organization.enabled ?
                        <span {...{theme: 'badge success pill'}}>Active Subscription</span> :
                        <></>}
                </div>
                    <div className={"flex cursor-pointer"}
                         id={"avatars-" + organization.id}
                         onClick={() => navigate('/users?organizationId=' + organization.id)}
                    >
                        { users.map((user, index) => (
                            <Avatar key={index}
                                    name={`${user.firstName} ${user.lastName}`}
                                    className={"bg-gray-200 border-2 border-white"} style={{zIndex: (3-index), margin: "-5px"}}/>
                        ))}
                        <Tooltip for={"avatars-" + organization.id}
                                 text={`${userCount} Members`} position={"bottom"}
                                 hoverDelay={100}
                                 hideDelay={100}/>
                    </div>
            </section>
        </div>
    );
}

export default OrganizationItem;