import React, {CSSProperties} from 'react';
import type Organization from "Frontend/generated/rize/os/access/manager/organization/Organization.js";
import { Button } from '@vaadin/react-components/Button.js';
import { ContextMenu } from '@vaadin/react-components/ContextMenu.js';
import Apartment from '@mui/icons-material/Apartment';
import {MoreVert} from "@mui/icons-material";

interface OrganizationItemProps {
    organization: Organization,
    className?: string,
    style?: CSSProperties
}

const separatorStyle = {
    height: "1px",
    width: "100%"
};

const items = [{ text: 'Edit' }, { text: 'Delete' }];

const OrganizationItem: React.FC<OrganizationItemProps> = ({ organization , className, style}) => {
    return (
        <div className={"border border-contrast-10 rounded-l flex flex-col " + className} style={style}>
            <section className={"flex flex-row gap-m justify-center p-m items-center"}>
                <div className={"flex"}>
                    <div className={"grid bg-contrast-20 rounded-m w-xl h-xl content-center justify-center"}>
                        <Apartment className={"text-tertiary"} style={{transform: "scale(1.6)"}}/>
                    </div>
                </div>
                <div className={"flex-grow flex flex-col"}>
                    <span className={"text-l text-body font-bold leading-xs"}>{organization.name}</span>
                    <span className={"text-s text-secondary leading-xs"}>{organization.alias}</span>
                </div>
                <div>
                    <ContextMenu items={items} openOn="click" onItemSelected={e => console.log(e)}>
                        <Button className={"text-secondary"} theme="icon tertiary">
                            <MoreVert/>
                        </Button>
                    </ContextMenu>
                </div>
            </section>
            <div className={"bg-contrast-10"} style={separatorStyle}/>
            <section className={"p-m"}>
                {organization.enabled ?
                    <span {...{theme: 'badge success pill'}} className={"flex-shrink"}>Active Subscription</span> :
                    <></>}
            </section>
        </div>
    );
}

export default OrganizationItem;