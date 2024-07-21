import { Dialog, Button, TextField, Icon, DialogOpenedChangedEvent } from "@vaadin/react-components";
import  Organization from "Frontend/generated/rize/os/access/manager/organization/Organization";
import React, {useEffect, useState} from "react";
import Apartment from "@mui/icons-material/Apartment";
import {OrganizationEndpoint} from "Frontend/generated/endpoints";

interface OrganizationDialogProps {
    mode?: "edit" | "create"
    organization?: Organization,
    opened?: boolean,
    onClose?: () => void,
    onCreate?: (organization: Organization) => void,
    onUpdate?: (organization: Organization) => void
}

const OrganizationDialog: React.FC<OrganizationDialogProps> = (props) => {

    const [isSaving, setIsSaving] = useState<boolean>(false);
    const [name, setName] = useState<string | undefined>(undefined);
    const [nameError, setNameError] = useState<string>("");
    const [alias, setAlias] = useState<string | undefined>(undefined);
    const [aliasError, setAliasError] = useState<string>("");
    const [opened, setOpened] = useState<boolean>(false);

    useEffect(() => {
        setOpened(props.opened?? true);
        if(props.opened)
            focusTextField("organization-name-text-field");
    }, [props.opened]);

    useEffect(() => {
        if (opened) {
            setName(props.organization?.name ?? undefined);
            setAlias(props.organization?.alias ?? undefined);
        }
    }, [props.organization, opened]);

    useEffect(() => {
        const delayDebounceFn = setTimeout(validateName, 500);
        setNameError("");
        return () => clearTimeout(delayDebounceFn);
    }, [name]);

    useEffect(() => {
        const delayDebounceFn = setTimeout(validateAlias, 500);
        setAliasError("");
        return () => clearTimeout(delayDebounceFn);
    }, [alias]);

    const validateName = async (): Promise<boolean> => {
        if (name !== undefined && name.length < 3) {
            setNameError("Name must be at least 3 characters long");
            return false;
        }

        if (name !== undefined && name.length > 64) {
            setNameError("Name cannot be longer than 64 characters");
            return false;
        }

        if (name !== undefined && !/^[a-zA-Z0-9][a-zA-Z0-9-_&+./ ]*[a-zA-Z0-9]$/.test(name)) {
            setNameError("Name must start and end with a letter or number and be less than 32 characters long");
            return false;
        }

        if (name !== props.organization?.name && await OrganizationEndpoint.nameExists(name ?? "")) {
            setNameError("Name already exists");
            return false;
        }

        setNameError("");
        return true;
    }

    const validateAlias = async (): Promise<boolean> => {
        if (alias !== undefined && alias.length < 3) {
            setAliasError("Alias must be at least 3 characters long");
            return false;
        }

        if (alias !== undefined && alias.length > 64) {
            setAliasError("Alias cannot be longer than 64 characters");
            return false;
        }

        if (alias !== undefined && !/^[a-z0-9][a-z0-9-]*[a-z0-9]$/.test(alias)) {
            setAliasError("Alias must start and end with a letter or number");
            return false;
        }

        if (alias !== props.organization?.alias && await OrganizationEndpoint.aliasExists(alias ?? "")) {
            setAliasError("Alias already exists");
            return false;
        }

        setAliasError("");
        return true;
    }

    const focusTextField = (id: string) => {
        setTimeout(() => {
            document.getElementById(id)?.focus();
        }, 50);
    }


    const handleOpenedChanged = (e: DialogOpenedChangedEvent) => {
        if (!e.detail.value)
            props.onClose?.();
    };

    const handleSave = async () => {
        if (name === undefined || alias === undefined) {
            setName(name ?? "");
            setAlias(alias ?? "");
            return;
        }

        if (!await validateName() || !await validateAlias())
            return;

        setIsSaving(true);
        if (props.mode === "create")
            await createOrganization();
        else
            await updateOrganization();

        setIsSaving(false);
        setOpened(false)
    };

    const createOrganization = async () => {
        const organization = await OrganizationEndpoint.create(!name? "" : name, !alias? "" : alias);
        props.onCreate?.(organization);
    }

    const updateOrganization = async () => {
        if (!props.organization)
            return;

        let updatedOrganization= props.organization;

        if (updatedOrganization.name !== name)
            updatedOrganization = await OrganizationEndpoint.updateName(updatedOrganization.id, name ?? "");
        if (updatedOrganization.alias !== alias)
            updatedOrganization = await OrganizationEndpoint.updateAlias(updatedOrganization.id, alias ?? "");

        props.onUpdate?.(updatedOrganization);
    }

    return (
        <Dialog onOpenedChanged={handleOpenedChanged}
                opened={opened}
                noCloseOnEsc
                noCloseOnOutsideClick
                footerRenderer={() => (
                    <>
                        <Button onClick={() => setOpened(false)}>Cancel</Button>
                        <Button onClick={handleSave} theme={"primary"}>
                            {isSaving ? <Icon icon={"vaadin:spinner"} className={"animate-spin"}/> : <></>}
                            Save
                        </Button>
                    </>
                )}>
            <div className={"grid gap-x-6 gap-y-4"}>
                <section className={"col-span-2"}>
                    <span className={"text-2xl font-bold"}>{props.mode === "create" ? "Create Organization" : "Edit Organization"}</span>
                </section>

                <section className={"content-center"}>
                    <div className={"grid bg-gray-300 rounded-lg w-28 h-28 content-center justify-center"}>
                        <Apartment className={"text-gray-500"} style={{transform: "scale(3.2)"}}/>
                    </div>
                </section>

                <section className={"flex flex-col w-72"}>
                    <TextField id={"organization-name-text-field"}
                               value={name} key={"name"}
                               label={"Name"}
                               invalid={nameError.length > 0}
                               errorMessage={nameError}
                               onValueChanged={e => setName(e.detail.value)}
                               allowedCharPattern={"[a-zA-Z0-9-_&+./ ]"}
                               className={"pt-0"}/>

                    <TextField id={"organization-alias-text-field"}
                               value={alias}
                               label={"Alias"}
                               invalid={aliasError.length > 0}
                               errorMessage={aliasError}
                               onValueChanged={e => setAlias(e.detail.value)}
                               allowedCharPattern={"[a-z0-9-]"}/>
                </section>

                <section className={"col-span-2"}>
                    {props.organization?.enabled ?
                        <span {...{theme: 'badge success pill'}} className={"flex-shrink"}>Active Subscription</span> :
                        <></>}
                </section>
            </div>
        </Dialog>
    );
}

export default OrganizationDialog;