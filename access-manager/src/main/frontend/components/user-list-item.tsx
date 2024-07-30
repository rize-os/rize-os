import React, { useEffect, useState } from 'react';
import { Avatar, MenuBar, MenuBarItemSelectedEvent } from '@vaadin/react-components';
import type User from "Frontend/generated/rize/os/access/manager/user/User";
import { Edit, MoreVert, AccountCircle } from "@mui/icons-material";

interface UserListItemProps {
    user: User
}

const menuBarItems = [
    { component: <MoreVert/>,
        className: "text-gray-500 rounded-full p-0 m-0",
        theme: "icon tertiary",
        children: [
            { component: <><AccountCircle style={{transform: "scale(0.8)"}}/><p className={"mx-2"}>Details</p></>, key: "details"},
            { component: <><Edit style={{transform: "scale(0.8)"}}/><p className={"mx-2"}>Edit</p></>, key: "edit"}
        ]
    }
]

const UserListItem: React.FC<UserListItemProps> = ({ user }) => {

    const username = () => {
        return user.username === user.email ? user.username : `@${user.username}`
    }

    return (
        <div>
            <div className={"flex flex-row gap-3.5 justify-center py-2 items-center cursor-default"}>
                <Avatar name={`${user.firstName} ${user.lastName}`} className={"cursor-default"}/>
                <div className={"flex-grow flex flex-col"}>
                    <span className={"text-m text-gray-700 font-bold leading-5"}>{`${user.firstName} ${user.lastName}`}</span>
                    <span className={"text-xs text-gray-400 leading-4"}>{username()}</span>
                </div>
                <div className={"flex-shrink"}>
                    {!user.enabled && <span {...{theme: 'badge error pill'}}>Disabled</span>}
                </div>
                <div>
                    <MenuBar theme="icon" items={menuBarItems}/>
                </div>
            </div>
        </div>
    );
}

export default UserListItem;