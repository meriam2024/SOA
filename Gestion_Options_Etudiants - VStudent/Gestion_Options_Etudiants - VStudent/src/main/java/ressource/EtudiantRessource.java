package Ressources;

import entities.Etudiant;
import entities.EtudiantList;
import entities.Option;
import metiers.EtudiantBusiness;
import metiers.OptionBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/etudiants")
public class EtudiantRessource {

    private EtudiantBusiness etudiantBusiness = new EtudiantBusiness();
    private OptionBusiness optionBusiness = new OptionBusiness();

    // GET /rest/etudiants
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Etudiant> getEtudiants() {
        return etudiantBusiness.getAllEtudiants();
    }

    // GET /rest/etudiants/{identifiant}
    @GET
    @Path("/{identifiant}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEtudiantById(@PathParam("identifiant") String identifiant) {
        Etudiant e = etudiantBusiness.getEtudiantByIdentifiant(identifiant);
        if (e != null)
            return Response.status(Response.Status.OK).entity(e).build();
        return Response.status(Response.Status.NOT_FOUND).entity("etudiant not found").build();
    }

    // GET /rest/etudiants/option?codeOption=1  (XML)
    @GET
    @Path("/option")
    @Produces(MediaType.APPLICATION_XML)
    public Response getEtudiantsByOption(@QueryParam("codeOption") int codeOption) {
        Option opt = optionBusiness.getOptionByCode(codeOption);
        if (opt == null)
            return Response.status(Response.Status.NOT_FOUND).entity("opt not found").build();

        List<Etudiant> result = new ArrayList<>();
        for (Etudiant e : etudiantBusiness.getEtudiantsByOption(opt)) {
            result.add(new Etudiant(e.getIdentifiant(), e.getNom(), e.getPrenom(),
                    null, e.getAnneeEtude(), e.getEmail()));
        }
        return Response.status(Response.Status.OK).entity(new EtudiantList(result)).build();
    }

    // POST /rest/etudiants
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEtudiant(Etudiant etudiant) {
        if (etudiant == null || etudiant.getIdentifiant() == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("identifiant is required").build();
        if (etudiantBusiness.getEtudiantByIdentifiant(etudiant.getIdentifiant()) != null)
            return Response.status(Response.Status.CONFLICT).entity("etudiant already exists").build();
        if (etudiant.getOption() == null
                || optionBusiness.getOptionByCode(etudiant.getOption().getCodeOption()) == null)
            return Response.status(Response.Status.NOT_FOUND).entity("opt not found").build();

        if (etudiantBusiness.addEtudiant(etudiant))
            return Response.status(Response.Status.CREATED).entity(etudiant).build();
        return Response.status(Response.Status.NOT_FOUND).entity("opt not found").build();
    }

    // PUT /rest/etudiants/{identifiant}
    @PUT
    @Path("/{identifiant}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEtudiant(@PathParam("identifiant") String identifiant, Etudiant updated) {
        if (etudiantBusiness.getEtudiantByIdentifiant(identifiant) == null)
            return Response.status(Response.Status.NOT_FOUND).entity("etudiant not found").build();
        if (updated == null || updated.getOption() == null)
            return Response.status(Response.Status.NOT_FOUND).entity("opt not found").build();

        Option opt = optionBusiness.getOptionByCode(updated.getOption().getCodeOption());
        if (opt == null)
            return Response.status(Response.Status.NOT_FOUND).entity("opt not found").build();

        updated.setIdentifiant(identifiant);
        updated.setOption(opt);
        if (etudiantBusiness.updateEtudiant(identifiant, updated))
            return Response.status(Response.Status.OK).entity(updated).build();
        return Response.status(Response.Status.NOT_FOUND).entity("etudiant not found").build();
    }

    // DELETE /rest/etudiants/{identifiant}
    @DELETE
    @Path("/{identifiant}")
    public Response deleteEtudiant(@PathParam("identifiant") String identifiant) {
        if (etudiantBusiness.deleteEtudiant(identifiant))
            return Response.status(Response.Status.OK).entity("etudiant deleted").build();
        return Response.status(Response.Status.NOT_FOUND).entity("etudiant not found").build();
    }
}