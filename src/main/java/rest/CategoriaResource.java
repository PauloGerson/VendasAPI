package rest;

import org.acme.Categoria;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/categoria")
public class CategoriaResource {

    private Categoria categoria;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public List <Categoria> list() {
        return Categoria.listAll();
    }

    @Transactional
    @POST
    public void insert(Categoria categoria) {
        categoria.persistAndFlush();
    }

    @Transactional
    @PUT
    public void update(Categoria categoria) {
       Categoria localizar = Categoria.findById(categoria.getId());
        if (localizar == null) {
            throw  new WebApplicationException("Categoria não localizada.", Response.Status.NOT_FOUND);
        }
        localizar.setDescricao(categoria.getDescricao());
        localizar.persistAndFlush();
        //categoria.persistAndFlush();
    }

    @Transactional
    @Path("{id}")
    @DELETE
    public void delete(@PathParam("id") long id){
        try {
            Categoria localizar = Categoria.findById(id);
            if (localizar == null) {
                throw  new WebApplicationException("Categoria não localizada.", Response.Status.NOT_FOUND);
            }
            localizar.delete();
        }
        catch (ConstraintViolationException e){
            if(e.getMessage().contains("PRODUTO FOREIGN KEY(CATEGORIA_ID)")){
                throw new WebApplicationException(
                        "Não é possivel excluir a Categoria pois existem produtos relacionados",
                        Response.Status.CONFLICT);
            }
        }

        catch (RuntimeException e) {
            Logger.getLogger("categoria").log(Level.ALL, e.getMessage());
        }
    }
}