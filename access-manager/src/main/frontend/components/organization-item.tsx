import React, {CSSProperties} from 'react';
import type Organization from "Frontend/generated/rize/os/access/manager/organization/Organization.js";
import {MenuBar, MenuBarItemSelectedEvent} from '@vaadin/react-components/MenuBar.js';
import Apartment from '@mui/icons-material/Apartment';
import { MoreVert, Edit, Delete } from "@mui/icons-material";

interface OrganizationItemProps {
    organization: Organization,
    className?: string,
    style?: CSSProperties,
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
            { component: <><Edit style={{transform: "scale(0.8)"}}/><p className={"mx-2"}>Edit</p></>, key: "edit"},
            { component: <><Delete style={{transform: "scale(0.8)"}}/><p className={"mx-2"}>Delete</p></> , key: "delete"}
        ]
    }
]

const OrganizationItem: React.FC<OrganizationItemProps> = ({ organization , className, style, onEdit}) => {

    const handleMenuItemSelected = (e: MenuBarItemSelectedEvent) => {
        // @ts-ignore
        if (e.detail.value.key === "edit")
            onEdit?.(organization);
    }

    return (
        <div className={"border border-contrast-10 rounded-xl flex flex-col box-border " + className} style={style}>
            <section className={"flex flex-row gap-3.5 justify-center p-4 items-center"}>
                <div className={"flex"}>
                    <div className={"grid bg-gray-300 rounded-lg w-14 h-14 content-center justify-center"}>
                        <Apartment className={"text-gray-500"} style={{transform: "scale(1.6)"}}/>
                    </div>
                </div>
                <div className={"flex-grow flex flex-col"}>
                    <span className={"text-xl text-gray-700 font-bold leading-6"}>{organization.name}</span>
                    <span className={"text-sm text-gray-400 leading-5"}>{organization.alias}</span>
                </div>
                <div>
                    <MenuBar theme="icon" items={menuBarItems} onItemSelected={handleMenuItemSelected}/>
                </div>
            </section>

            <div className={"bg-gray-200"} style={separatorStyle}/>

            <section className={"px-4 py-3.5"}>
                {organization.enabled ?
                    <span {...{theme: 'badge success pill'}} className={"flex-shrink"}>Active Subscription</span> :
                    <></>}
            </section>
        </div>
    );
}

export default OrganizationItem;